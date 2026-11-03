package com.handbook.microservices.healthcheck.cached;

import com.handbook.microservices.support.FakeClock;

import java.time.Duration;

public class CachedHealthDemo {

    public static void main(String[] args) {
        System.out.println("=== Streaming: Probes Every Second, Database Checked Every 10 Seconds ===");
        FakeClock clock = FakeClock.at("2026-11-01T20:00:00Z");
        int[] dbQueries = {0};
        CachedHealthCheck check = new CachedHealthCheck(() -> {
            dbQueries[0]++;
            return true; // SELECT 1 against the catalog DB
        }, clock, Duration.ofSeconds(10));

        int probes = 0;
        for (int second = 0; second < 60; second++) {
            for (int source = 0; source < 5; source++) { // kubelet + load balancer + mesh + 2 monitors
                check.healthy();
                probes++;
            }
            clock.advance(Duration.ofSeconds(1));
        }
        System.out.println("Health probes in one minute: " + probes);
        System.out.println("Queries the database actually received: " + dbQueries[0]);
        System.out.println("Across 200 pods: " + probes * 200 + " probes -> " + dbQueries[0] * 200 + " DB queries per minute");
    }
}
