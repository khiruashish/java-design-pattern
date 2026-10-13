package com.handbook.capstone.ordering;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/** [Rate Limiter] Token bucket per customer for checkout attempts (bots, double-taps, scripts). */
public final class TokenBucket {

    private final int capacity;
    private final double refillPerSecond;
    private final Clock clock;
    private final Map<String, double[]> buckets = new HashMap<>(); // key → {tokens, lastMillis}

    public TokenBucket(int capacity, double refillPerSecond, Clock clock) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.clock = clock;
    }

    public synchronized boolean tryAcquire(String key) {
        long now = clock.millis();
        double[] b = buckets.computeIfAbsent(key, k -> new double[] {capacity, now});
        b[0] = Math.min(capacity, b[0] + (now - b[1]) / 1000.0 * refillPerSecond);
        b[1] = now;
        if (b[0] >= 1) {
            b[0] -= 1;
            return true;
        }
        return false;
    }
}
