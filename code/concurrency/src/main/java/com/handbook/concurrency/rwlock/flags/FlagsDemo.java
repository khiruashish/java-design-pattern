package com.handbook.concurrency.rwlock.flags;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FlagsDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS: Feature-Flag Cache (8 readers, 1 refresher) ===");
        FeatureFlags flags = new FeatureFlags();
        AtomicInteger reads = new AtomicInteger();
        AtomicInteger halfApplied = new AtomicInteger();

        try (ExecutorService ex = Executors.newFixedThreadPool(9)) {
            for (int r = 0; r < 8; r++) {
                ex.submit(() -> {
                    for (int i = 0; i < 20_000; i++) {
                        boolean[] pair = flags.checkoutAndPayments();
                        if (pair[0] != pair[1]) {
                            halfApplied.incrementAndGet();
                        }
                        reads.incrementAndGet();
                    }
                });
            }
            ex.submit(() -> {
                for (int i = 0; i < 200; i++) {
                    flags.refresh(i % 2 == 0);
                }
            });
        }
        System.out.println("Reads                  : " + reads.get());
        System.out.println("Refreshes              : " + flags.version());
        System.out.println("Half-applied flag pairs: " + halfApplied.get());
    }
}
