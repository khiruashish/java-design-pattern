package com.handbook.concurrency.rwlock.location;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.StampedLock;

/** A driver's position, updated every second, read thousands of times by matching and ETA services. */
public class DriverLocation {

    private final StampedLock lock = new StampedLock();
    private double lat = 12.9300;
    private double lng = 77.6200;
    private final AtomicLong fallbacks = new AtomicLong(); // many readers can be in the read lock at once

    public void move(double newLat, double newLng) {
        long stamp = lock.writeLock();
        try {
            lat = newLat;
            lng = newLng;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /** Optimistic read: no locking at all unless a write happened meanwhile. */
    public double[] position() {
        long stamp = lock.tryOptimisticRead(); // (1)!
        double la = lat;
        double ln = lng;
        if (!lock.validate(stamp)) { // (2)!
            stamp = lock.readLock();
            try {
                fallbacks.incrementAndGet();
                la = lat;
                ln = lng;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return new double[] {la, ln};
    }

    public long fallbacks() {
        return fallbacks.get();
    }
}
