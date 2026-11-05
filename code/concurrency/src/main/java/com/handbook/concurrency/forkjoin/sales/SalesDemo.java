package com.handbook.concurrency.forkjoin.sales;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.LongStream;

public class SalesDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery Analytics: Summing 5 Million Order Values ===");
        long[] orders = new Random(42).longs(5_000_000, 5_000, 500_000).toArray(); // INR 50 to INR 5,000

        long sequential = LongStream.of(orders).sum();
        try (ForkJoinPool pool = new ForkJoinPool(4)) {
            long forkJoin = pool.invoke(new SalesTotal(orders, 0, orders.length));
            System.out.printf("Sequential total : INR %,d%n", sequential / 100);
            System.out.printf("Fork/Join total  : INR %,d%n", forkJoin / 100);
            System.out.println("Results match    : " + (sequential == forkJoin));
            System.out.printf("Parallel stream  : INR %,d (same framework underneath)%n", LongStream.of(orders).parallel().sum() / 100);
        }
    }
}
