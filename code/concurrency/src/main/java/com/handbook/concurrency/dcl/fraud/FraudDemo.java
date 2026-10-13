package com.handbook.concurrency.dcl.fraud;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FraudDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Lazily Loaded Fraud Model ===");
        FraudScorer scorer = new FraudScorer();
        System.out.println("Service started; model loads so far: " + FraudScorer.Model.LOADS.get());

        AtomicInteger flagged = new AtomicInteger();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 500; i++) { // the first burst of payments arrives at once
                int amount = (i % 10 == 0) ? 60_000 : 900;
                boolean newDevice = i % 10 == 0;
                ex.submit(() -> {
                    if (scorer.isFraud(amount, newDevice)) {
                        flagged.incrementAndGet();
                    }
                });
            }
        }
        System.out.println("Payments scored : 500");
        System.out.println("Flagged as fraud: " + flagged.get());
        System.out.println("Model loads     : " + FraudScorer.Model.LOADS.get() + " (despite 500 concurrent first calls)");
    }
}
