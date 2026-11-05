package com.handbook.microservices.competingconsumers.invoices;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** N identical workers pull from ONE queue; each message goes to exactly one of them. */
public final class InvoiceWorkers {

    private static final String STOP = "__STOP__";

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final Map<String, Integer> processedCount = new ConcurrentHashMap<>();
    private final AtomicInteger inProgress = new AtomicInteger();
    private final AtomicInteger maxParallel = new AtomicInteger();

    public void publish(String orderId) {
        queue.add(orderId);
    }

    public void runWith(int workers) throws InterruptedException {
        try (ExecutorService pool = Executors.newFixedThreadPool(workers)) {
            for (int i = 0; i < workers; i++) {
                queue.add(STOP); // one poison pill per worker, after the real work
            }
            for (int i = 0; i < workers; i++) {
                pool.submit(this::workLoop);
            }
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private Void workLoop() throws InterruptedException {
        while (true) {
            String orderId = queue.take(); // (1)!
            if (orderId.equals(STOP)) {
                return null;
            }
            int now = inProgress.incrementAndGet();
            maxParallel.accumulateAndGet(now, Math::max);
            Thread.sleep(2); // render the PDF
            processedCount.merge(orderId, 1, Integer::sum);
            inProgress.decrementAndGet();
        }
    }

    public Map<String, Integer> processedCount() {
        return processedCount;
    }

    public int maxParallel() {
        return maxParallel.get();
    }
}
