package com.handbook.concurrency.threadpool.bulkhead;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** One small pool per tenant: a noisy tenant can exhaust only its own capacity. */
public class BulkheadDemo {

    static ExecutorService tenantPool() {
        return new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== SaaS: Per-Tenant Bulkheads for Data Exports ===");
        Map<String, ExecutorService> pools = Map.of("acme", tenantPool(), "globex", tenantPool());
        CountDownLatch acmeHugeExports = new CountDownLatch(1);

        int acmeRejected = 0;
        for (int i = 0; i < 10; i++) { // acme floods the system with huge exports
            try {
                pools.get("acme").submit(() -> { acmeHugeExports.await(); return null; });
            } catch (RejectedExecutionException e) {
                acmeRejected++;
            }
        }
        System.out.println("acme: 10 huge exports requested, " + acmeRejected + " rejected (its bulkhead is full)");

        Future<String> globex = pools.get("globex").submit(() -> "invoice export done"); // (1)!
        System.out.println("globex: " + globex.get(2, TimeUnit.SECONDS) + " (unaffected by acme)");

        acmeHugeExports.countDown();
        for (ExecutorService p : pools.values()) {
            p.shutdown();
            p.awaitTermination(5, TimeUnit.SECONDS);
        }
        System.out.println("all pools shut down");
    }
}
