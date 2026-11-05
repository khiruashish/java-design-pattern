package com.handbook.concurrency.guarded.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SettlementDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Payments: Balking Duplicate Settlement Triggers ===");
        CountDownLatch release = new CountDownLatch(1);
        SettlementJob job = new SettlementJob(release);

        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Boolean> cron = pool.submit(job::trigger);   // scheduler fires at 23:00
            while (!job.isRunning()) {
                Thread.onSpinWait();                             // wait until the scheduled run has started
            }
            List<Future<Boolean>> others = new ArrayList<>();
            others.add(pool.submit(job::trigger));               // ops clicks "run now"
            others.add(pool.submit(job::trigger));               // a retry from the orchestrator
            int balked = 0;
            for (Future<Boolean> f : others) {
                balked += f.get() ? 0 : 1;                       // they return at once, no waiting
            }
            System.out.println("While running: " + balked + " duplicate triggers balked immediately");
            release.countDown();
            System.out.println("Scheduled run settled? " + cron.get());
        }
        System.out.println("Batches settled: " + job.batchesSettled() + " (never twice: no double payouts)");
        System.out.println("Next night's trigger runs again: " + new SettlementJob(new CountDownLatch(0)).trigger());
    }
}
