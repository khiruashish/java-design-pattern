package com.handbook.microservices.ratelimit.distributed;

import com.handbook.microservices.support.FakeClock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class DistributedLimiterDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T10:00:00Z");
        System.out.println("=== Ticketing: concert sale, 4 seat-hold requests per user per minute ===");

        List<GatewayNode> local = List.of(
                new GatewayNode("gw-1", null, 4, clock), new GatewayNode("gw-2", null, 4, clock),
                new GatewayNode("gw-3", null, 4, clock));
        System.out.println("Per-node counters : " + send(12, i -> local.get(i % 3).handleLocalOnly()));

        SharedCounterStore redis = new SharedCounterStore();
        List<GatewayNode> shared = List.of(
                new GatewayNode("gw-1", redis, 4, clock), new GatewayNode("gw-2", redis, 4, clock),
                new GatewayNode("gw-3", redis, 4, clock));
        System.out.println("Shared counter    : " + send(12, i -> shared.get(i % 3).handleShared("FAN-9")));
        System.out.println("Allowed: per-node 12 of 12 (3x the limit!), shared 4 of 12");

        clock.advanceMillis(60_000);
        System.out.println("Next minute       : " + send(2, i -> shared.get(i % 3).handleShared("FAN-9")));
    }

    private static List<String> send(int n, IntFunction<String> request) {
        List<String> results = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            results.add(request.apply(i));
        }
        return results;
    }
}
