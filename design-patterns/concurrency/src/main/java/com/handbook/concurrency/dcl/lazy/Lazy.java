package com.handbook.concurrency.dcl.lazy;

import java.util.Objects;
import java.util.function.Supplier;

/** Challenge solution: a thread-safe memoizing supplier (like Kotlin's lazy or Guava's memoize). */
public final class Lazy<T> implements Supplier<T> {

    private volatile Supplier<? extends T> initializer; // cleared after use so captured objects can be GC'd
    private volatile T value;

    private Lazy(Supplier<? extends T> initializer) {
        this.initializer = Objects.requireNonNull(initializer);
    }

    public static <T> Lazy<T> of(Supplier<? extends T> initializer) {
        return new Lazy<>(initializer);
    }

    @Override
    public T get() {
        T v = value;
        if (v == null) {
            synchronized (this) {
                v = value;
                if (v == null) {
                    v = Objects.requireNonNull(initializer.get(), "initializer returned null");
                    value = v;            // publish (volatile write)
                    initializer = null;   // release the lambda and whatever it captured
                }
            }
        }
        return v;
    }

    public boolean isInitialized() {
        return value != null;
    }
}
