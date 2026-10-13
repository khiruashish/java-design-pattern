package com.handbook.microservices.tracing.threads;

import com.handbook.microservices.tracing.core.TraceContext;

import java.util.concurrent.Executor;

/**
 * The current trace context usually lives in a ThreadLocal. Work submitted to another thread loses
 * it unless the executor captures the context at submit time and restores it in the worker.
 */
public final class ContextPropagatingExecutor implements Executor {

    public static final ThreadLocal<TraceContext> CURRENT = new ThreadLocal<>();

    private final Executor delegate;

    public ContextPropagatingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable task) {
        TraceContext captured = CURRENT.get(); // (1)!
        delegate.execute(() -> {
            TraceContext previous = CURRENT.get();
            CURRENT.set(captured); // (2)!
            try {
                task.run();
            } finally {
                CURRENT.set(previous); // (3)!
            }
        });
    }
}
