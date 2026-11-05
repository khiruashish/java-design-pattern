package com.handbook.concurrency.rwlock.custom;

/** Challenge solution: a read-write lock with writer preference, built on wait/notifyAll. */
public class SimpleReadWriteLock {

    private int activeReaders;
    private boolean writerActive;
    private int waitingWriters;

    public synchronized void lockRead() throws InterruptedException {
        while (writerActive || waitingWriters > 0) { // writer preference: don't starve writers
            wait();
        }
        activeReaders++;
    }

    public synchronized void unlockRead() {
        activeReaders--;
        if (activeReaders == 0) {
            notifyAll();
        }
    }

    public synchronized void lockWrite() throws InterruptedException {
        waitingWriters++;
        try {
            while (writerActive || activeReaders > 0) {
                wait();
            }
        } finally {
            waitingWriters--;
        }
        writerActive = true;
    }

    public synchronized void unlockWrite() {
        writerActive = false;
        notifyAll();
    }
}
