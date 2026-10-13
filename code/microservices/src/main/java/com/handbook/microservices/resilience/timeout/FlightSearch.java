package com.handbook.microservices.resilience.timeout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Searches several airline suppliers in parallel. Each call gets its own deadline, so one slow
 * supplier can't hold the whole search page hostage.
 */
public final class FlightSearch {

    public record Result(List<String> fares, List<String> timedOut, List<String> failed) {
    }

    private final ExecutorService pool;
    private final long timeoutMillis;

    public FlightSearch(ExecutorService pool, long timeoutMillis) {
        this.pool = pool;
        this.timeoutMillis = timeoutMillis;
    }

    public Result search(Map<String, Supplier<String>> suppliers) {
        List<String> fares = new ArrayList<>();
        List<String> timedOut = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        Map<String, CompletableFuture<String>> calls = new LinkedHashMap<>();
        suppliers.forEach((name, call) -> calls.put(name, CompletableFuture.supplyAsync(call, pool)
                .orTimeout(timeoutMillis, TimeUnit.MILLISECONDS))); // (1)!

        calls.forEach((name, future) -> {
            try {
                fares.add(future.join());
            } catch (RuntimeException e) {
                (e.getCause() instanceof TimeoutException ? timedOut : failed).add(name); // (2)!
            }
        });
        return new Result(fares, timedOut, failed);
    }
}
