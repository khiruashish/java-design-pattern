package com.handbook.microservices.sidecar.traffic;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * The client-side sidecar (Envoy) applies routing rules pushed by the mesh control plane:
 * weighted subsets for canaries, and retries for failed calls. The calling app just calls "recommendations".
 */
public final class TrafficSplitSidecar {

    private final TreeMap<Integer, String> cumulativeWeights = new TreeMap<>();
    private final Map<String, Function<String, String>> subsets;
    private final int retries;
    private final Random random;
    private int total;

    public TrafficSplitSidecar(Map<String, Integer> weights, Map<String, Function<String, String>> subsets,
                               int retries, Random random) {
        weights.forEach((subset, w) -> {
            total += w;
            cumulativeWeights.put(total, subset); // (1)!
        });
        this.subsets = subsets;
        this.retries = retries;
        this.random = random;
    }

    public String call(String request) {
        RuntimeException last = null;
        for (int attempt = 0; attempt <= retries; attempt++) { // (2)!
            String subset = cumulativeWeights.higherEntry(random.nextInt(total)).getValue();
            try {
                return subsets.get(subset).apply(request);
            } catch (RuntimeException e) {
                last = e;
            }
        }
        throw last;
    }
}
