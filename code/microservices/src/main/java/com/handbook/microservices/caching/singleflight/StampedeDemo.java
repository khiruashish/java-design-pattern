package com.handbook.microservices.caching.singleflight;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class StampedeDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Streaming: 500 Viewers Open a Just-Released Episode ===");
        AtomicInteger dbLoads = new AtomicInteger();
        CountDownLatch slowDb = new CountDownLatch(1);
        SingleFlightLoader<String> metadata = new SingleFlightLoader<>(id -> {
            dbLoads.incrementAndGet();
            try {
                slowDb.await(); // the metadata query takes a while
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return id + ": 'The Monsoon Heist' S2E1, 52 min, 4K";
        });

        List<Future<String>> viewers = new ArrayList<>();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 500; i++) {
                viewers.add(pool.submit(() -> metadata.get("ep-2201")));
            }
            while (dbLoads.get() == 0) {
                Thread.onSpinWait();
            }
            Thread.sleep(100); // let the other 499 requests pile up behind the in-flight load
            slowDb.countDown();
            long served = 0;
            for (Future<String> f : viewers) {
                served += f.get().startsWith("ep-2201") ? 1 : 0;
            }
            System.out.println("Viewers served: " + served);
        }
        System.out.println("Database loads: " + dbLoads.get() + " (without coalescing: up to 500)");
    }
}
