package com.handbook.microservices.ratelimit.slidingcounter;

import com.handbook.microservices.support.FakeClock;

public class SlidingCounterDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: fixed window vs. sliding window counter (limit 10/min) ===");
        FakeClock clock = FakeClock.at("2026-10-13T10:00:50Z");
        var sliding = new SlidingWindowCounter(10, 60_000, clock);
        long[] fixedCounts = new long[2];

        int slidingAllowed = 0;
        int fixedAllowed = 0;
        for (int burst = 0; burst < 2; burst++) { // 10 requests at 10:00:50, 10 more at 10:01:05
            for (int i = 0; i < 10; i++) {
                if (sliding.tryAcquire()) {
                    slidingAllowed++;
                }
                if (fixedCounts[burst]++ < 10) {
                    fixedAllowed++;
                }
            }
            clock.advanceMillis(15_000);
        }
        System.out.println("20 requests within 15 seconds, straddling the minute boundary:");
        System.out.println("Fixed window allowed  : " + fixedAllowed + "  <- 2x the limit in 15 s");
        System.out.println("Sliding window allowed: " + slidingAllowed);
    }
}
