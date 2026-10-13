package com.handbook.microservices.resilience.backoff;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Coding challenge: compare backoff strategies and see why jitter spreads out retry storms. */
public final class BackoffSchedule {

    private BackoffSchedule() {
    }

    public static List<Long> exponential(long base, long cap, int retries) {
        List<Long> waits = new ArrayList<>();
        for (int i = 1; i <= retries; i++) {
            waits.add(Math.min(cap, base * (1L << i)));
        }
        return waits;
    }

    public static List<Long> fullJitter(long base, long cap, int retries, Random random) {
        return exponential(base, cap, retries).stream()
                .map(ceiling -> (long) (random.nextDouble() * ceiling))
                .toList();
    }

    /** AWS "decorrelated jitter": sleep = min(cap, random_between(base, previous * 3)). */
    public static List<Long> decorrelatedJitter(long base, long cap, int retries, Random random) {
        List<Long> waits = new ArrayList<>();
        long previous = base;
        for (int i = 0; i < retries; i++) {
            long upper = previous * 3;
            previous = Math.min(cap, base + (long) (random.nextDouble() * (upper - base)));
            waits.add(previous);
        }
        return waits;
    }
}
