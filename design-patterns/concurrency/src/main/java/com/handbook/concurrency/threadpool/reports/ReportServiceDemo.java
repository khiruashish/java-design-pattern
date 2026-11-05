package com.handbook.concurrency.threadpool.reports;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Saturation made visible: 2 workers + 2 queue slots; the 5th and 6th requests are rejected. */
public class ReportServiceDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Banking: Statement Report Generation Under Load ===");
        CountDownLatch slowDatabase = new CountDownLatch(1); // holds the first reports "in progress"
        AtomicInteger finished = new AtomicInteger();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2), new ThreadPoolExecutor.AbortPolicy()); // (1)!

        for (int i = 1; i <= 6; i++) {
            String report = "STMT-" + i;
            try {
                pool.execute(() -> {
                    try {
                        slowDatabase.await();
                        finished.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                System.out.printf("%s accepted (running=%d, queued=%d)%n", report,
                        Math.min(pool.getPoolSize(), 2), pool.getQueue().size());
            } catch (RejectedExecutionException e) {
                System.out.println(report + " REJECTED -> HTTP 429, please retry in a minute"); // (2)!
            }
        }
        slowDatabase.countDown();
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Reports completed: " + finished.get());
    }
}
