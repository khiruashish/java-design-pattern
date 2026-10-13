package com.handbook.microservices.circuitbreaker.mini;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

/** Coding challenge: a minimal, thread-safe breaker that opens after N consecutive failures. */
public final class MiniCircuitBreaker {

    private final int failureThreshold;
    private final long openMillis;
    private final LongSupplier nowMillis;
    private int consecutiveFailures;
    private long openedAt = -1;

    public MiniCircuitBreaker(int failureThreshold, long openMillis, LongSupplier nowMillis) {
        this.failureThreshold = failureThreshold;
        this.openMillis = openMillis;
        this.nowMillis = nowMillis;
    }

    public synchronized <T> T call(Supplier<T> action, Supplier<T> fallback) {
        boolean open = openedAt >= 0 && nowMillis.getAsLong() - openedAt < openMillis;
        if (open) {
            return fallback.get();
        }
        // CLOSED, or the open period is over (HALF_OPEN): let this call through as a trial.
        try {
            T result = action.get();
            consecutiveFailures = 0;
            openedAt = -1;
            return result;
        } catch (RuntimeException e) {
            if (++consecutiveFailures >= failureThreshold || openedAt >= 0) {
                openedAt = nowMillis.getAsLong(); // trip, or re-trip after a failed trial
            }
            return fallback.get();
        }
    }

    public synchronized String state() {
        if (openedAt < 0) {
            return "CLOSED";
        }
        return nowMillis.getAsLong() - openedAt < openMillis ? "OPEN" : "HALF_OPEN";
    }
}
