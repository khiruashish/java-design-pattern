package com.handbook.concurrency.rwlock.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Read-mostly cache: thousands of reads per second, a refresh every few seconds. */
public class FeatureFlags {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, Boolean> flags = new HashMap<>(Map.of("checkout-v2", false, "payments-v2", false));
    private int version;

    /** Both flags must always be read together: the release turns them on as a pair. */
    public boolean[] checkoutAndPayments() {
        lock.readLock().lock(); // (1)!
        try {
            return new boolean[] {flags.get("checkout-v2"), flags.get("payments-v2")};
        } finally {
            lock.readLock().unlock();
        }
    }

    public void refresh(boolean enabled) {
        lock.writeLock().lock(); // (2)!
        try {
            flags.put("checkout-v2", enabled);
            Thread.onSpinWait(); // a reader sneaking in here would see a half-applied release
            flags.put("payments-v2", enabled);
            version++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int version() {
        lock.readLock().lock();
        try {
            return version;
        } finally {
            lock.readLock().unlock();
        }
    }
}
