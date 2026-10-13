package com.handbook.concurrency.virtualthreads.sessions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/** 10,000 customers watching their delivery: one blocked virtual thread per session. */
public class TrackingSessionsDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Logistics: 10,000 Live Tracking Sessions ===");
        int sessions = 10_000;
        List<BlockingQueue<String>> inboxes = new ArrayList<>();
        CountDownLatch allWaiting = new CountDownLatch(sessions);
        AtomicInteger delivered = new AtomicInteger();

        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < sessions; i++) {
                BlockingQueue<String> inbox = new ArrayBlockingQueue<>(1);
                inboxes.add(inbox);
                ex.submit(() -> {
                    allWaiting.countDown();
                    String update = inbox.take(); // (1)!
                    if (update.startsWith("OUT_FOR_DELIVERY")) {
                        delivered.incrementAndGet(); // would push to the customer's WebSocket
                    }
                    return null;
                });
            }
            allWaiting.await();
            System.out.println("Sessions blocked waiting for updates: " + sessions);
            for (BlockingQueue<String> inbox : inboxes) {
                inbox.put("OUT_FOR_DELIVERY rider 1.2 km away");
            }
        }
        System.out.println("Sessions that received their update : " + delivered.get());
        System.out.println("Platform threads that would need    : " + sessions + " x ~1 MB stack");
    }
}
