package com.handbook.concurrency.activeobject.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** A shared thermal printer: one worker, a BOUNDED activation queue, futures for each job. */
public final class LabelPrinter implements AutoCloseable {

    private final ThreadPoolExecutor worker;
    private final List<String> printed = new ArrayList<>(); // worker-thread only
    private final CountDownLatch paperLoaded;

    public LabelPrinter(int queueCapacity, CountDownLatch paperLoaded) {
        this.paperLoaded = paperLoaded;
        this.worker = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueCapacity), new ThreadPoolExecutor.AbortPolicy()); // (1)!
    }

    public CompletableFuture<Integer> print(String awb) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                await();
                printed.add(awb);
                return printed.size();
            }, worker);
        } catch (RejectedExecutionException e) { // (2)!
            return CompletableFuture.failedFuture(new IllegalStateException("printer queue full, retry " + awb + " later"));
        }
    }

    public CompletableFuture<List<String>> printedSoFar() {
        return CompletableFuture.supplyAsync(() -> List.copyOf(printed), worker);
    }

    private void await() {
        try {
            paperLoaded.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        worker.close();
    }
}
