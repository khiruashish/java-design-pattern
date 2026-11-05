package com.handbook.concurrency.activeobject.generic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/** Coding challenge solution: wrap ANY non-thread-safe object as an active object. */
public final class Actor<S> implements AutoCloseable {

    private final S state; // confined to the actor's thread
    private final ExecutorService thread = Executors.newSingleThreadExecutor(Thread.ofVirtual().factory());

    public Actor(S state) {
        this.state = state;
    }

    public <R> CompletableFuture<R> ask(Function<S, R> message) {
        return CompletableFuture.supplyAsync(() -> message.apply(state), thread);
    }

    @Override
    public void close() {
        thread.close();
    }
}
