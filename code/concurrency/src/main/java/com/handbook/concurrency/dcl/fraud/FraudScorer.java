package com.handbook.concurrency.dcl.fraud;

import java.util.concurrent.atomic.AtomicInteger;

/** The fraud model is 400 MB and takes seconds to load; load it once, on first use, per scorer. */
public class FraudScorer {

    static final class Model {
        static final AtomicInteger LOADS = new AtomicInteger();
        private final double threshold;

        Model() {
            LOADS.incrementAndGet();
            try {
                Thread.sleep(50); // reading weights from disk
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.threshold = 0.8;
        }

        double score(int amountInr, boolean newDevice) {
            return Math.min(1.0, amountInr / 100_000.0 + (newDevice ? 0.5 : 0));
        }
    }

    private volatile Model model; // (1)!

    private Model model() {
        Model m = model; // (2)!
        if (m == null) { // first check, no lock
            synchronized (this) {
                m = model;
                if (m == null) { // second check, under the lock
                    model = m = new Model();
                }
            }
        }
        return m;
    }

    public boolean isFraud(int amountInr, boolean newDevice) {
        Model m = model();
        return m.score(amountInr, newDevice) >= m.threshold;
    }
}
