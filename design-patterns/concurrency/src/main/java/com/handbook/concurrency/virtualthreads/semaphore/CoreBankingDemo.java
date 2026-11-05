package com.handbook.concurrency.virtualthreads.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/** Threads are cheap now; the core banking system is not. Limit the RESOURCE, not the threads. */
public class CoreBankingDemo {

    private static final Semaphore CORE_BANKING_SLOTS = new Semaphore(20); // (1)!
    private static final AtomicInteger inFlight = new AtomicInteger();
    private static final AtomicInteger peak = new AtomicInteger();

    static long balance(String account) throws InterruptedException {
        CORE_BANKING_SLOTS.acquire(); // (2)!
        try {
            peak.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
            Thread.sleep(20); // mainframe round trip
            return 10_000 + account.hashCode() % 1_000;
        } finally {
            inFlight.decrementAndGet();
            CORE_BANKING_SLOTS.release();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Banking: 1,000 Balance Checks, at Most 20 at the Core Banking System ===");
        AtomicInteger done = new AtomicInteger();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                String account = "SB-" + (1_000 + i);
                ex.submit(() -> {
                    balance(account);
                    done.incrementAndGet();
                    return null;
                });
            }
        }
        System.out.println("Virtual threads started  : 1000");
        System.out.println("Balance checks completed : " + done.get());
        System.out.println("Peak concurrent mainframe calls <= 20: " + (peak.get() <= 20));
        System.out.println("Limit was actually reached: " + (peak.get() == 20));
    }
}
