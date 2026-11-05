package com.handbook.microservices.deployment.canary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Progressive delivery: shift traffic in steps and compare canary vs baseline error rates before each step. */
public final class CanaryAnalysis {

    public record StepResult(int percent, double baselineErrorPct, double canaryErrorPct, String decision) { }

    private final int[] steps;
    private final double maxErrorIncreasePct;

    public CanaryAnalysis(int[] steps, double maxErrorIncreasePct) {
        this.steps = steps;
        this.maxErrorIncreasePct = maxErrorIncreasePct;
    }

    /** Simulates 10,000 requests per step with the given true error probabilities (seeded, so reproducible). */
    public List<StepResult> run(double baselineErrorRate, double canaryErrorRate, long seed) {
        Random random = new Random(seed);
        List<StepResult> results = new ArrayList<>();
        for (int percent : steps) {
            int canaryRequests = 10_000 * percent / 100;
            int baselineRequests = 10_000 - canaryRequests;
            double b = errors(random, baselineRequests, baselineErrorRate) * 100.0 / Math.max(1, baselineRequests);
            double c = errors(random, canaryRequests, canaryErrorRate) * 100.0 / Math.max(1, canaryRequests);
            boolean healthy = c - b <= maxErrorIncreasePct; // (1)!
            boolean last = percent == steps[steps.length - 1];
            results.add(new StepResult(percent, b, c, healthy ? (last ? "PROMOTE to 100%" : "continue") : "ROLLBACK"));
            if (!healthy) {
                break; // (2)!
            }
        }
        return results;
    }

    private static int errors(Random random, int requests, double rate) {
        int n = 0;
        for (int i = 0; i < requests; i++) {
            n += random.nextDouble() < rate ? 1 : 0;
        }
        return n;
    }
}
