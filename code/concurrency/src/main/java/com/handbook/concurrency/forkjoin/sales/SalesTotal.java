package com.handbook.concurrency.forkjoin.sales;

import java.util.concurrent.RecursiveTask;

/** Divide and conquer: split the range until it's small, sum directly, then combine. */
public final class SalesTotal extends RecursiveTask<Long> {

    static final int THRESHOLD = 50_000; // (1)!

    private final long[] orderPaise;
    private final int from;
    private final int to;

    public SalesTotal(long[] orderPaise, int from, int to) {
        this.orderPaise = orderPaise;
        this.from = from;
        this.to = to;
    }

    @Override
    protected Long compute() {
        if (to - from <= THRESHOLD) {
            long sum = 0;
            for (int i = from; i < to; i++) {
                sum += orderPaise[i];
            }
            return sum;
        }
        int mid = (from + to) >>> 1;
        SalesTotal left = new SalesTotal(orderPaise, from, mid);
        SalesTotal right = new SalesTotal(orderPaise, mid, to);
        left.fork();                       // (2)!
        long rightSum = right.compute();   // (3)!
        return left.join() + rightSum;
    }
}
