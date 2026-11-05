package com.handbook.concurrency.guarded.settlement;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** Balking: if the job is already running, a new trigger returns immediately instead of waiting. */
public final class SettlementJob {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger batchesSettled = new AtomicInteger();
    private final CountDownLatch holdOpen;

    public SettlementJob(CountDownLatch holdOpen) {
        this.holdOpen = holdOpen;
    }

    /** @return true if this call ran the settlement, false if it balked. */
    public boolean trigger() throws InterruptedException {
        if (!running.compareAndSet(false, true)) { // (1)!
            return false;
        }
        try {
            holdOpen.await(); // simulates a long settlement run
            batchesSettled.incrementAndGet();
            return true;
        } finally {
            running.set(false); // (2)!
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public int batchesSettled() {
        return batchesSettled.get();
    }
}
