package com.handbook.microservices.backpressure.shedding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class SheddingDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Banking: Statement API Protects the Core-Banking DB (max 3 in flight) ===");
        ConcurrencyLimiter limiter = new ConcurrencyLimiter(3);
        CountDownLatch slowCoreBanking = new CountDownLatch(1);
        AtomicInteger started = new AtomicInteger();

        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> first = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                int n = i;
                first.add(pool.submit(() -> limiter.handle(() -> {
                    started.incrementAndGet();
                    try {
                        slowCoreBanking.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "200 statement-" + n;
                })));
            }
            while (started.get() < 3) {
                Thread.onSpinWait(); // wait until 3 requests occupy all permits
            }
            System.out.println("request 4 -> " + limiter.handle(() -> "200 statement-4"));
            System.out.println("request 5 -> " + limiter.handle(() -> "200 statement-5"));
            slowCoreBanking.countDown();
            for (Future<String> f : first) {
                System.out.println("in-flight -> " + f.get());
            }
            System.out.println("request 6 -> " + limiter.handle(() -> "200 statement-6") + " (capacity is back)");
        }
    }
}
