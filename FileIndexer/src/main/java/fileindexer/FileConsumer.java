package fileindexer;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class FileConsumer implements Runnable {
    private final BlockingQueue<File> queue;
    private final FileIndex index;
    private final Statistics stats;
    private final File POISON;

    public FileConsumer(BlockingQueue<File> queue, FileIndex index, Statistics stats, File poison) {
        this.queue = queue;
        this.index = index;
        this.stats = stats;
        this.POISON = poison;
    }

    @Override
    public void run() {
        try {
            while (true) {
                File f = queue.take();
                if (f == POISON) {
                    queue.put(POISON); 
                    break;
                }
                FileData data = new FileData(f);
                if (index.add(data)) stats.totalIndexed.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
