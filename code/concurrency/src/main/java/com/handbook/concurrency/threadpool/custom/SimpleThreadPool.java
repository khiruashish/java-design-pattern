package com.handbook.concurrency.threadpool.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/** Challenge solution: a minimal fixed thread pool, to understand what ThreadPoolExecutor does. */
public class SimpleThreadPool {

    private static final Runnable SHUTDOWN = () -> { };

    private final BlockingQueue<Runnable> tasks;
    private final List<Thread> workers = new ArrayList<>();
    private volatile boolean accepting = true;

    public SimpleThreadPool(int threads, int queueCapacity) {
        tasks = new LinkedBlockingQueue<>(queueCapacity);
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(this::workLoop, "simple-pool-" + (i + 1));
            workers.add(t);
            t.start();
        }
    }

    private void workLoop() {
        try {
            while (true) {
                Runnable task = tasks.take();
                if (task == SHUTDOWN) {
                    return;
                }
                try {
                    task.run();
                } catch (RuntimeException e) { // a failing task must not kill the worker
                    System.out.println("task failed: " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void execute(Runnable task) throws InterruptedException {
        if (!accepting) {
            throw new IllegalStateException("pool is shut down");
        }
        tasks.put(task); // blocks when the queue is full (back-pressure)
    }

    public void shutdownAndWait() throws InterruptedException {
        accepting = false;
        for (int i = 0; i < workers.size(); i++) {
            tasks.put(SHUTDOWN); // queued after all real tasks, so pending work completes first
        }
        for (Thread t : workers) {
            t.join();
        }
    }
}
