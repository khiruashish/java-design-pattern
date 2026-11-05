package com.handbook.concurrency.activeobject.wallet;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Active Object: a proxy whose methods enqueue requests that ONE scheduler thread executes. */
public final class WalletActor implements AutoCloseable {

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor(); // (1)!
    private long balancePaise; // touched only by the scheduler thread: no locks, no volatile
    private int operations;

    public CompletableFuture<Long> credit(long paise) { // (2)!
        return CompletableFuture.supplyAsync(() -> {
            balancePaise += paise;
            operations++;
            return balancePaise;
        }, scheduler);
    }

    public CompletableFuture<Boolean> debit(long paise) {
        return CompletableFuture.supplyAsync(() -> {
            operations++;
            if (paise > balancePaise) {
                return false; // the check and the update can't interleave with anything else
            }
            balancePaise -= paise;
            return true;
        }, scheduler);
    }

    public CompletableFuture<String> statement() {
        return CompletableFuture.supplyAsync(() -> "balance=" + balancePaise + " after " + operations + " ops", scheduler);
    }

    @Override
    public void close() {
        scheduler.close(); // (3)!
    }
}
