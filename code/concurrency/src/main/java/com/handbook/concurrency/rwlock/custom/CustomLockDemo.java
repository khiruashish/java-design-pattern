package com.handbook.concurrency.rwlock.custom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomLockDemo {

    static int balanceA = 500;
    static int balanceB = 500; // transfers keep A + B = 1000

    public static void main(String[] args) {
        SimpleReadWriteLock lock = new SimpleReadWriteLock();
        AtomicInteger wrongTotals = new AtomicInteger();
        try (ExecutorService ex = Executors.newFixedThreadPool(4)) {
            ex.submit(() -> {
                for (int i = 0; i < 2_000; i++) {
                    lock.lockWrite();
                    try {
                        balanceA -= 10;
                        balanceB += 10;
                    } finally {
                        lock.unlockWrite();
                    }
                }
                return null;
            });
            for (int r = 0; r < 3; r++) {
                ex.submit(() -> {
                    for (int i = 0; i < 20_000; i++) {
                        lock.lockRead();
                        try {
                            if (balanceA + balanceB != 1_000) {
                                wrongTotals.incrementAndGet();
                            }
                        } finally {
                            lock.unlockRead();
                        }
                    }
                    return null;
                });
            }
        }
        System.out.println("Final A + B       : " + (balanceA + balanceB));
        System.out.println("Inconsistent reads: " + wrongTotals.get());
    }
}
