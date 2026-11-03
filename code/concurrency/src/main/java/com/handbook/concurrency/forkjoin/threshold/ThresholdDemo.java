package com.handbook.concurrency.forkjoin.threshold;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

/** Coding challenge: what the threshold does to the number of tasks. */
public class ThresholdDemo {

    static final class Count extends RecursiveTask<Long> {
        final int from;
        final int to;
        final int threshold;
        final AtomicLong tasks;

        Count(int from, int to, int threshold, AtomicLong tasks) {
            this.from = from;
            this.to = to;
            this.threshold = threshold;
            this.tasks = tasks;
            tasks.incrementAndGet();
        }

        @Override
        protected Long compute() {
            if (to - from <= threshold) {
                long evens = 0;
                for (int i = from; i < to; i++) {
                    evens += (i & 1) == 0 ? 1 : 0;
                }
                return evens;
            }
            int mid = (from + to) >>> 1;
            Count left = new Count(from, mid, threshold, tasks);
            left.fork();
            return new Count(mid, to, threshold, tasks).compute() + left.join();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: Choosing a Fork/Join Threshold (10,000,000 elements) ===");
        try (ForkJoinPool pool = new ForkJoinPool(4)) {
            for (int threshold : new int[] {1, 1_000, 100_000, 10_000_000}) {
                AtomicLong tasks = new AtomicLong();
                long evens = pool.invoke(new Count(0, 10_000_000, threshold, tasks));
                System.out.printf("threshold %,10d -> %,10d tasks created, result %,d%n", threshold, tasks.get(), evens);
            }
        }
        System.out.println("Too small: millions of tiny tasks (overhead dominates). Too big: one task, no parallelism.");
    }
}
