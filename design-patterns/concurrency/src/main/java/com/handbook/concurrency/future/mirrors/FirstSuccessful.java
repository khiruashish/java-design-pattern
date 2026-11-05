package com.handbook.concurrency.future.mirrors;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/** Challenge solution: complete with the first SUCCESSFUL result; fail only if every attempt fails. */
public final class FirstSuccessful {

    private FirstSuccessful() {
    }

    public static <T> CompletableFuture<T> of(List<CompletableFuture<T>> attempts) {
        CompletableFuture<T> result = new CompletableFuture<>();
        AtomicInteger failures = new AtomicInteger();
        for (CompletableFuture<T> attempt : attempts) {
            attempt.whenComplete((value, error) -> {
                if (error == null) {
                    result.complete(value); // first success wins; later calls are ignored
                } else if (failures.incrementAndGet() == attempts.size()) {
                    result.completeExceptionally(new IllegalStateException("all " + attempts.size() + " attempts failed", error));
                }
            });
        }
        return result;
    }
}
