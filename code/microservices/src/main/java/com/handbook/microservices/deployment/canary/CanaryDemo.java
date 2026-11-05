package com.handbook.microservices.deployment.canary;

import com.handbook.microservices.deployment.canary.CanaryAnalysis.StepResult;

public class CanaryDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: Canary Rollout of a New Fare Engine (steps 1% -> 5% -> 25% -> 50%, then promote) ===");
        CanaryAnalysis analysis = new CanaryAnalysis(new int[] {1, 5, 25, 50}, 0.5);

        System.out.println("Release 7.3 (healthy):");
        for (StepResult r : analysis.run(0.004, 0.0045, 11)) {
            print(r);
        }
        System.out.println("Release 7.4 (bug: fares fail for airport pickups, ~3% of rides):");
        for (StepResult r : analysis.run(0.004, 0.034, 11)) {
            print(r);
        }
        System.out.println("The bad release was stopped at 1% of rides, automatically, instead of reaching everyone.");
    }

    private static void print(StepResult r) {
        System.out.printf("  %3d%% canary: baseline %.2f%% errors, canary %.2f%% -> %s%n",
                r.percent(), r.baselineErrorPct(), r.canaryErrorPct(), r.decision());
    }
}
