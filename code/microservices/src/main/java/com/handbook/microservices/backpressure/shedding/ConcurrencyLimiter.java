package com.handbook.microservices.backpressure.shedding;

import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

/** Load shedding at the door: at most N requests in flight; the rest are refused fast, not queued forever. */
public final class ConcurrencyLimiter {

    private final Semaphore permits;

    public ConcurrencyLimiter(int maxInFlight) {
        this.permits = new Semaphore(maxInFlight);
    }

    public String handle(Supplier<String> work) {
        if (!permits.tryAcquire()) { // (1)!
            return "503 Service Unavailable (shed, Retry-After: 1)";
        }
        try {
            return work.get();
        } finally {
            permits.release();
        }
    }
}
