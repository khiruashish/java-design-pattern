package com.handbook.microservices.backpressure.unbounded;

/** Coding challenge: what an unbounded queue does when arrivals exceed capacity for a minute. */
public class UnboundedQueueDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: Unbounded vs Bounded Queue During a 60s Spike ===");
        int arrivalsPerSec = 1_000;
        int servicePerSec = 600;
        int bound = 1_200; // 2 seconds of work

        long unboundedBacklog = 0;
        long boundedBacklog = 0;
        long rejected = 0;
        for (int second = 1; second <= 60; second++) {
            unboundedBacklog += arrivalsPerSec - servicePerSec;
            long room = bound - boundedBacklog;
            long accepted = Math.min(arrivalsPerSec, room + servicePerSec);
            rejected += arrivalsPerSec - accepted;
            boundedBacklog = Math.min(bound, boundedBacklog + accepted - servicePerSec);
            if (second == 10 || second == 60) {
                System.out.printf("t=%2ds unbounded backlog=%,6d (wait %4.0fs) | bounded backlog=%,d (wait %.0fs), rejected so far=%,d%n",
                        second, unboundedBacklog, unboundedBacklog / (double) servicePerSec, boundedBacklog,
                        boundedBacklog / (double) servicePerSec, rejected);
            }
        }
        System.out.println("Unbounded: every request is accepted and served 40s late (most clients already timed out), then OOM.");
        System.out.println("Bounded  : excess is refused at once with 429, clients back off, accepted work stays fast.");
    }
}
