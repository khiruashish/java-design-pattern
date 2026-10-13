package com.handbook.concurrency.virtualthreads.fanout;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/** 2,000 blocking calls to a pincode-availability service, 100 ms each. */
public class AvailabilityDemo {

    static boolean available(int pincode) throws InterruptedException {
        Thread.sleep(100); // blocking I/O: an HTTP call to the availability service
        return pincode % 7 != 0;
    }

    static long run(ExecutorService executor, AtomicInteger availableCount) {
        long start = System.nanoTime();
        try (executor) { // (1)!
            for (int i = 0; i < 2_000; i++) {
                int pincode = 560_000 + i;
                executor.submit(() -> {
                    if (available(pincode)) {
                        availableCount.incrementAndGet();
                    }
                    return null;
                });
            }
        }
        return Duration.ofNanos(System.nanoTime() - start).toMillis();
    }

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Checking Delivery Availability for 2,000 Pincodes ===");
        AtomicInteger platformCount = new AtomicInteger();
        AtomicInteger virtualCount = new AtomicInteger();

        long platformMs = run(Executors.newFixedThreadPool(50), platformCount);       // (2)!
        long virtualMs = run(Executors.newVirtualThreadPerTaskExecutor(), virtualCount); // (3)!

        System.out.println("Serviceable pincodes (both runs agree): " + (platformCount.get() == virtualCount.get())
                + ", " + virtualCount.get() + " of 2000");
        System.out.println("50 platform threads took about 4 s       : " + (platformMs >= 3_500));
        System.out.println("Virtual threads finished in under 1.5 s  : " + (virtualMs < 1_500));
        System.out.println("Virtual threads at least 5x faster       : " + (platformMs >= 5 * virtualMs));
    }
}
