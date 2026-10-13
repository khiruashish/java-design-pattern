package com.handbook.microservices.strangler.banking;

import java.util.Set;
import java.util.stream.IntStream;

public class PercentageRolloutDemo {

    public static void main(String[] args) {
        var rollout = new PercentageRollout(id -> "legacy", id -> "modern", Set.of("EMP-1"));

        System.out.println("=== Banking: moving statements to the new service by percentage ===");
        for (int percent : new int[] {0, 5, 25, 100}) {
            rollout.setPercent(percent);
            System.out.printf("%3d%% -> %4d of 10,000 customers on the new service; EMP-1 -> %s; CUST-1001 -> %s%n",
                    percent, modernCount(rollout), rollout.statement("EMP-1"), rollout.statement("CUST-1001"));
        }
        rollout.setKillSwitch(true);
        System.out.printf("KILL -> %4d of 10,000 customers on the new service (instant rollback)%n", modernCount(rollout));
    }

    private static long modernCount(PercentageRollout rollout) {
        return IntStream.range(0, 10_000).mapToObj(i -> "CUST-" + i)
                .filter(id -> rollout.statement(id).equals("modern")).count();
    }
}
