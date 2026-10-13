package com.handbook.microservices.ratelimit.tokenbucket;

import java.time.Clock;

/**
 * Token bucket: holds up to {@code capacity} tokens, refilled continuously at {@code refillPerSecond}.
 * Each request takes one token. Allows short bursts up to capacity, then a steady average rate.
 */
public final class TokenBucket {

    private final long capacity;
    private final double refillPerMilli;
    private final Clock clock;
    private double tokens;
    private long lastRefill;

    public TokenBucket(long capacity, double refillPerSecond, Clock clock) {
        this.capacity = capacity;
        this.refillPerMilli = refillPerSecond / 1000.0;
        this.clock = clock;
        this.tokens = capacity; // (1)!
        this.lastRefill = clock.millis();
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    public synchronized long tokensLeft() {
        refill();
        return (long) tokens;
    }

    private void refill() { // (2)!
        long now = clock.millis();
        tokens = Math.min(capacity, tokens + (now - lastRefill) * refillPerMilli);
        lastRefill = now;
    }
}
