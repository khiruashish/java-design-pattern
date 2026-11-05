package com.handbook.concurrency.producerconsumer.gps;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** Many riders' phones produce GPS pings; one writer batches them into the database. */
public class GpsIngestDemo {

    record Ping(String rider, double lat, double lng) {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Logistics: Batched GPS Ingestion ===");
        BlockingQueue<Ping> queue = new LinkedBlockingQueue<>(10_000);
        AtomicBoolean producersDone = new AtomicBoolean();
        AtomicInteger written = new AtomicInteger();
        AtomicInteger batches = new AtomicInteger();
        AtomicInteger biggestBatch = new AtomicInteger();

        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            pool.submit(() -> { // single consumer: batch writer
                List<Ping> batch = new ArrayList<>(100);
                while (!producersDone.get() || !queue.isEmpty()) {
                    Ping first = queue.poll(20, TimeUnit.MILLISECONDS); // wait briefly for the first item
                    if (first == null) {
                        continue;
                    }
                    batch.add(first);
                    queue.drainTo(batch, 99); // (1)!
                    written.addAndGet(batch.size()); // one INSERT ... VALUES (...), (...), ...
                    batches.incrementAndGet();
                    biggestBatch.accumulateAndGet(batch.size(), Math::max);
                    batch.clear();
                }
                return null;
            });

            try (ExecutorService riders = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int r = 0; r < 200; r++) { // 200 riders x 25 pings
                    String rider = "R" + r;
                    riders.submit(() -> {
                        for (int i = 0; i < 25; i++) {
                            queue.put(new Ping(rider, 12.9 + i * 0.0001, 77.6));
                        }
                        return null;
                    });
                }
            }
            producersDone.set(true);
        }

        System.out.println("Pings received      : 5000");
        System.out.println("Pings written       : " + written.get());
        System.out.println("Batch size <= 100   : " + (biggestBatch.get() <= 100));
        System.out.println("Fewer DB writes than pings: " + (batches.get() < 5_000));
    }
}
