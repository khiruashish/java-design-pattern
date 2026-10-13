package com.handbook.creational.objectpool.generic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** Challenge solution: a small, thread-safe, generic object pool. */
public final class ObjectPool<T> {

    private final BlockingQueue<T> idle = new LinkedBlockingQueue<>();
    private final Semaphore permits;
    private final Supplier<T> factory;
    private final Predicate<T> validator;
    private final Consumer<T> reset;

    public ObjectPool(int maxSize, Supplier<T> factory, Predicate<T> validator, Consumer<T> reset) {
        this.permits = new Semaphore(maxSize, true); // fair: first come, first served
        this.factory = factory;
        this.validator = validator;
        this.reset = reset;
    }

    public T borrow(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (!permits.tryAcquire(timeout, unit)) {
            throw new TimeoutException("Pool exhausted");
        }
        try {
            T obj;
            while ((obj = idle.poll()) != null) {
                if (validator.test(obj)) {
                    return obj;
                }
            }
            return factory.get();
        } catch (RuntimeException e) {
            permits.release(); // creation failed: give the permit back
            throw e;
        }
    }

    public void giveBack(T obj) {
        try {
            reset.accept(obj);
            idle.offer(obj);
        } finally {
            permits.release();
        }
    }

    public int idleCount() {
        return idle.size();
    }
}
