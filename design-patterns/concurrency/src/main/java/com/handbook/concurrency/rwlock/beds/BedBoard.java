package com.handbook.concurrency.rwlock.beds;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Hospital bed board: nurses read it constantly; admissions update it. Shows downgrading. */
public class BedBoard {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, String> beds = new TreeMap<>(Map.of("ICU-1", "free", "ICU-2", "P-1001", "WARD-7", "free"));

    /** Admit, then print the board WITHOUT letting another writer change it in between. */
    public String admitAndSnapshot(String bed, String patient) {
        lock.writeLock().lock();
        try {
            if (!"free".equals(beds.get(bed))) {
                return bed + " is occupied";
            }
            beds.put(bed, patient);
            lock.readLock().lock(); // (1)!
        } finally {
            lock.writeLock().unlock(); // (2)!
        }
        try {
            return "after admitting " + patient + ": " + beds;
        } finally {
            lock.readLock().unlock();
        }
    }

    /** The trap: trying to upgrade a read lock to a write lock. */
    public boolean tryUpgradeWhileReading() throws InterruptedException {
        lock.readLock().lock();
        try {
            return lock.writeLock().tryLock(100, TimeUnit.MILLISECONDS); // (3)!
        } finally {
            lock.readLock().unlock();
        }
    }
}
