package com.handbook.microservices.resilience.backoff;

import java.util.Random;
import java.util.stream.IntStream;

public class BackoffDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: backoff strategies (base 100 ms, cap 5 s, 5 retries) ===");
        System.out.println("Exponential         : " + BackoffSchedule.exponential(100, 5_000, 5));
        System.out.println("Full jitter         : " + BackoffSchedule.fullJitter(100, 5_000, 5, new Random(7)));
        System.out.println("Decorrelated jitter : " + BackoffSchedule.decorrelatedJitter(100, 5_000, 5, new Random(7)));

        System.out.println("-- 1,000 clients fail at the same moment; when does their 1st retry land? --");
        Random random = new Random(11);
        long sameMs = IntStream.range(0, 1_000)
                .mapToLong(i -> BackoffSchedule.exponential(100, 5_000, 1).get(0)).distinct().count();
        long jitteredMs = IntStream.range(0, 1_000)
                .mapToLong(i -> BackoffSchedule.fullJitter(100, 5_000, 1, random).get(0)).distinct().count();
        System.out.println("Without jitter: all 1,000 retries in " + sameMs + " distinct millisecond (a thundering herd)");
        System.out.println("With jitter   : retries spread over " + jitteredMs + " distinct milliseconds");
    }
}
