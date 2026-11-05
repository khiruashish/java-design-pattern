package com.handbook.microservices.leaderelection.fencing;

import com.handbook.microservices.leaderelection.fencing.FencedStorage.LockService;

public class FencingDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Fencing Tokens Stop a Zombie Leader ===");
        LockService locks = new LockService();
        FencedStorage payoutFile = new FencedStorage();

        long tokenA = locks.grant("payout-writer-A");
        System.out.println("A becomes leader with token " + tokenA + ", writes batch 1: " + payoutFile.write(tokenA, "batch-1 by A"));
        System.out.println("A pauses for 40s (stop-the-world GC); its lease expires");

        long tokenB = locks.grant("payout-writer-B");
        System.out.println("B becomes leader with token " + tokenB + ", writes batch 2: " + payoutFile.write(tokenB, "batch-2 by B"));

        boolean zombie = payoutFile.write(tokenA, "batch-2 by A (duplicate payouts!)");
        System.out.println("A wakes up, still thinks it's leader, writes with token " + tokenA + ": accepted=" + zombie);
        System.out.println("Payout file: " + payoutFile.written());
    }
}
