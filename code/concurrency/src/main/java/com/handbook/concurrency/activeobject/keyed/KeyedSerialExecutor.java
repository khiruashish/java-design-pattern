package com.handbook.concurrency.activeobject.keyed;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/** Many lightweight active objects: tasks for the SAME key run in order; different keys run in parallel. */
public final class KeyedSerialExecutor {

    private final Executor pool;
    private final Map<String, CompletableFuture<?>> tails = new ConcurrentHashMap<>();

    public KeyedSerialExecutor(Executor pool) {
        this.pool = pool;
    }

    public <T> CompletableFuture<T> submit(String key, Supplier<T> task) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] next = new CompletableFuture[1];
        tails.compute(key, (k, tail) -> { // (1)!
            CompletableFuture<?> previous = tail == null ? CompletableFuture.completedFuture(null) : tail;
            next[0] = previous.handleAsync((ignored, err) -> task.get(), pool); // (2)!
            return next[0];
        });
        return next[0];
    }
}
