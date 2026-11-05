package com.handbook.concurrency.immutable.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS API: Rate-Limit Config Updated While Serving Traffic ===");
        RateLimitConfig config = new RateLimitConfig();
        AtomicInteger reads = new AtomicInteger();
        AtomicInteger invalid = new AtomicInteger();

        try (ExecutorService ex = Executors.newFixedThreadPool(5)) {
            for (int r = 0; r < 4; r++) {
                ex.submit(() -> {
                    for (int i = 0; i < 100_000; i++) {
                        RateLimitConfig.Snapshot s = config.get(); // one consistent snapshot per request
                        if (s.burst() > s.requestsPerMinute() || s.burst() * 10 != s.requestsPerMinute()) {
                            invalid.incrementAndGet();
                        }
                        reads.incrementAndGet();
                    }
                });
            }
            ex.submit(() -> {
                for (int i = 1; i <= 1_000; i++) {
                    int rpm = (i % 2 == 0) ? 600 : 1_200;
                    config.update(rpm, rpm / 10);
                }
            });
        }
        System.out.println("Requests served       : " + reads.get());
        System.out.println("Config versions       : " + config.get().version());
        System.out.println("Inconsistent configs  : " + invalid.get());
        System.out.println("Locks taken by readers: 0");
    }
}
