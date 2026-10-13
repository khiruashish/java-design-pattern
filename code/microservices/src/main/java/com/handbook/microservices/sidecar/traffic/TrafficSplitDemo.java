package com.handbook.microservices.sidecar.traffic;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;

public class TrafficSplitDemo {

    public static void main(String[] args) {
        Map<String, Function<String, String>> subsets = Map.of(
                "v1", req -> "v1",
                "v2", req -> {
                    if (req.hashCode() % 20 == 0) {
                        throw new IllegalStateException("v2 503");
                    }
                    return "v2";
                });

        System.out.println("=== Streaming: canary of recommendations v2 via mesh routing rules ===");
        for (int v2Weight : new int[] {10, 50}) {
            Map<String, Integer> weights = new LinkedHashMap<>();
            weights.put("v1", 100 - v2Weight);
            weights.put("v2", v2Weight);
            var sidecar = new TrafficSplitSidecar(weights, subsets, 1, new Random(42));
            Map<String, Integer> served = new TreeMap<>();
            int failed = 0;
            for (int i = 0; i < 10_000; i++) {
                try {
                    served.merge(sidecar.call("profile-" + i), 1, Integer::sum);
                } catch (IllegalStateException e) {
                    failed++;
                }
            }
            System.out.println("weights v1=" + (100 - v2Weight) + "/v2=" + v2Weight + " -> served " + served
                    + ", failed after retry: " + failed);
        }
        System.out.println("The app code never changed: weights and retries are mesh configuration.");
    }
}
