package com.handbook.microservices.resilience.bulkhead;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BulkheadDemo {

    public static void main(String[] args) throws Exception {
        Bulkhead statements = new Bulkhead("statements", 3); // slow PDF statement generator
        Bulkhead payments = new Bulkhead("payments", 10);    // UPI / card payments

        CountDownLatch reportsRunning = new CountDownLatch(3);
        CountDownLatch reportsMayFinish = new CountDownLatch(1);

        System.out.println("=== Banking: slow statements must not block payments ===");
        List<Thread> slow = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String id = "STMT-" + i;
            slow.add(Thread.ofVirtual().start(() -> statements.call(() -> {
                reportsRunning.countDown();
                await(reportsMayFinish); // the statement service is very slow today
                return id;
            })));
        }
        reportsRunning.await();
        System.out.println("3 statement requests in flight; statements permits left: " + statements.available());

        try {
            statements.call(() -> "STMT-4");
        } catch (Bulkhead.FullException e) {
            System.out.println("STMT-4  -> rejected fast: " + e.getMessage() + " (client sees 503, retry later)");
        }
        System.out.println("PAY-101 -> " + payments.call(() -> "payment accepted") + " (separate bulkhead)");
        System.out.println("PAY-102 -> " + payments.call(() -> "payment accepted"));

        reportsMayFinish.countDown();
        for (Thread t : slow) {
            t.join();
        }
        System.out.println("Statements finished; permits left: " + statements.available());
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
