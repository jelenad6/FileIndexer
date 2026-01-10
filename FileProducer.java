package fileindexer;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class FileProducer implements Runnable {
    private final File root;
    private final BlockingQueue<File> queue;
    private final Statistics stats;
    private final long MAX_SIZE;
    private final String[] allowedExtensions;
    private final File POISON;

    // Deduplikaciju fajlova po putanji
    private static final Set<String> seenPaths = ConcurrentHashMap.newKeySet();

    public FileProducer(File root, BlockingQueue<File> queue, Statistics stats,
                        long maxSize, String[] allowedExtensions, File poison) {
        this.root = root;
        this.queue = queue;
        this.stats = stats;
        this.MAX_SIZE = maxSize;
        this.allowedExtensions = allowedExtensions;
        this.POISON = poison;
    }

    @Override
    public void run() {
        try {
            crawl(root);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            try { queue.put(POISON); } catch (InterruptedException ignored) {}
        }
    }

    private void crawl(File dir) throws InterruptedException {
        if (dir.isHidden()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                crawl(f);
            } else {
                try {
                    String canonicalPath = f.getCanonicalPath();

                    // Ako je fajl vec vidjen, preskace se
                    if (!seenPaths.add(canonicalPath)) continue;

                    stats.totalFound.incrementAndGet();

                    if (f.isHidden()) { stats.skip("hidden"); continue; }
                    if (f.length() > MAX_SIZE) { stats.skip("too big"); continue; }

                    String name = f.getName();
                    int dot = name.lastIndexOf('.');
                    String ext = (dot == -1) ? "" : name.substring(dot+1);
                    boolean allowed = false;
                    for (String a : allowedExtensions) if (a.equalsIgnoreCase(ext)) allowed = true;
                    if (!allowed) { stats.skip("extension"); continue; }

                    queue.put(f); 

                } catch (IOException e) {
                    stats.skip("io exception");
                }
            }
        }
    }
}
