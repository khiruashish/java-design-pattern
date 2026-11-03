package com.handbook.microservices.leaderelection.lease;

import com.handbook.microservices.support.FakeClock;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LeaseDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: One of Three Pods Runs the Substitution Report ===");
        FakeClock clock = FakeClock.at("2026-09-01T02:00:00Z");
        DbLease lease = new DbLease(clock, Duration.ofSeconds(15));
        List<String> pods = new ArrayList<>(List.of("pod-1", "pod-2", "pod-3"));

        for (int tick = 0; tick < 6; tick++) {
            if (tick == 2) {
                pods.remove("pod-1");
                System.out.println("t=" + tick * 5 + "s pod-1 is killed (no graceful release)");
            }
            List<String> leaders = new ArrayList<>();
            for (String pod : pods) {
                if (lease.tryAcquireOrRenew(pod)) {
                    leaders.add(pod);
                }
            }
            System.out.println("t=" + tick * 5 + "s leader(s): " + (leaders.isEmpty() ? "none (lease not yet expired)" : leaders));
            clock.advance(Duration.ofSeconds(5));
        }
    }
}
