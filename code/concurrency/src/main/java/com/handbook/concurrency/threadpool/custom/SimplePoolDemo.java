package com.handbook.concurrency.threadpool.custom;

import java.util.concurrent.atomic.AtomicInteger;

public class SimplePoolDemo {

    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool pool = new SimpleThreadPool(3, 10);
        AtomicInteger done = new AtomicInteger();
        for (int i = 1; i <= 50; i++) {
            int n = i;
            pool.execute(() -> {
                if (n == 13) {
                    throw new IllegalStateException("task 13 hit a bad record");
                }
                done.incrementAndGet();
            });
        }
        pool.shutdownAndWait();
        System.out.println("Tasks completed: " + done.get() + " of 50 (one failed, workers survived)");
        try {
            pool.execute(() -> { });
        } catch (IllegalStateException e) {
            System.out.println("After shutdown: " + e.getMessage());
        }
    }
}
