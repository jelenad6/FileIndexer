package fileindexer;

import java.io.File;
import java.util.concurrent.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        System.out.print("Unesi putanju do direktorijuma: ");
        String path = sc.nextLine();

        File root = new File(path);
        if (!root.exists() || !root.isDirectory()) {
            System.err.println("Nevalidan direktorijum: " + path);
            System.exit(1);
        }

        // BlockingQueue
        BlockingQueue<File> queue = new ArrayBlockingQueue<>(100);
        FileIndex index = new FileIndex();
        Statistics stats = new Statistics();
        final long MAX_SIZE = 10_000_000; // 10 MB
        final String[] allowedExt = {"txt","java","md"};
        final File POISON = new File(""); // poison pill

        int producersCount = 2;
        int consumersCount = Runtime.getRuntime().availableProcessors();

        // CountDownLatch
        CountDownLatch doneLatch = new CountDownLatch(producersCount + consumersCount);

        // Pokretanje producer niti
        ExecutorService producers = Executors.newFixedThreadPool(producersCount);
        for (int i = 0; i < producersCount; i++) {
            producers.submit(() -> {
                try {
                    new FileProducer(root, queue, stats, MAX_SIZE, allowedExt, POISON).run();
                } finally {
                    doneLatch.countDown(); 
                }
            });
        }

        // Pokretanje consumer niti
        ExecutorService consumers = Executors.newFixedThreadPool(consumersCount);
        for (int i = 0; i < consumersCount; i++) {
            consumers.submit(() -> {
                try {
                    new FileConsumer(queue, index, stats, POISON).run();
                } finally {
                    doneLatch.countDown(); 
                }
            });
        }

        // Statistika
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Statistika: " + stats + ", Queue size: " + queue.size());
        }, 0, 1, TimeUnit.SECONDS);

        // Zatvaranje producer/consumer 
        producers.shutdown();
        consumers.shutdown();

        
        doneLatch.await();  

       
        scheduler.shutdown();

        
        System.out.println("Konacni indeks: " + index.size() + " datoteka");
        System.out.println("Konacna statistika: " + stats);
    }
}
