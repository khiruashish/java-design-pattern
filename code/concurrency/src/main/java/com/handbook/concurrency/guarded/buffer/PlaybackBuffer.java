package com.handbook.concurrency.guarded.buffer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** Guarded suspension with Lock/Condition: playback waits until enough seconds are buffered. */
public final class PlaybackBuffer {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition enoughBuffered = lock.newCondition(); // (1)!
    private int bufferedSeconds;

    public void append(int seconds) {
        lock.lock();
        try {
            bufferedSeconds += seconds;
            enoughBuffered.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /** @return true if playback can start, false if we should show a spinner / drop quality. */
    public boolean awaitPlayable(int neededSeconds, long timeoutMs) throws InterruptedException {
        lock.lock();
        try {
            long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutMs);
            while (bufferedSeconds < neededSeconds) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = enoughBuffered.awaitNanos(nanos); // (2)!
            }
            return true;
        } finally {
            lock.unlock();
        }
    }
}
