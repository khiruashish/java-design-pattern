package com.handbook.microservices.resilience.bulkhead;

import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

/**
 * A semaphore bulkhead: at most N concurrent calls to one dependency. Extra calls are rejected
 * at once instead of piling up and starving threads that other features need.
 */
public final class Bulkhead {

    public static final class FullException extends RuntimeException {
        FullException(String name) {
            super(name + " bulkhead full");
        }
    }

    private final String name;
    private final Semaphore permits;

    public Bulkhead(String name, int maxConcurrentCalls) {
        this.name = name;
        this.permits = new Semaphore(maxConcurrentCalls);
    }

    public <T> T call(Supplier<T> action) {
        if (!permits.tryAcquire()) { // (1)!
            throw new FullException(name);
        }
        try {
            return action.get();
        } finally {
            permits.release(); // (2)!
        }
    }

    public int available() {
        return permits.availablePermits();
    }
}
