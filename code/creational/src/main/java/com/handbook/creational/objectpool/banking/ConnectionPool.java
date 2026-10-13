package com.handbook.creational.objectpool.banking;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {

    private final BlockingQueue<DbConnection> idle; // (1)!
    private final AtomicInteger created = new AtomicInteger();
    private final int maxSize;

    public ConnectionPool(int maxSize) {
        this.maxSize = maxSize;
        this.idle = new ArrayBlockingQueue<>(maxSize);
    }

    /** A borrowed connection. Closing the lease returns it to the pool. */
    public final class Lease implements AutoCloseable { // (2)!
        private final DbConnection connection;
        private boolean returned;

        private Lease(DbConnection connection) {
            this.connection = connection;
        }

        public DbConnection connection() {
            return connection;
        }

        @Override
        public void close() {
            if (!returned) {
                returned = true;
                release(connection);
            }
        }
    }

    public Lease acquire(long timeoutMs) throws InterruptedException, TimeoutException {
        DbConnection c = idle.poll();
        while (c != null && !c.isValid()) { // (3)!
            System.out.println("  [pool] evicting broken connection #" + c.id());
            created.decrementAndGet();
            c = idle.poll();
        }
        if (c == null && created.incrementAndGet() <= maxSize) { // (4)!
            c = new DbConnection();
        } else if (c == null) {
            created.decrementAndGet();
            c = idle.poll(timeoutMs, TimeUnit.MILLISECONDS); // (5)!
            if (c == null) {
                throw new TimeoutException("No connection available within " + timeoutMs + " ms (max " + maxSize + ")");
            }
        }
        return new Lease(c);
    }

    private void release(DbConnection c) {
        c.reset(); // (6)!
        idle.offer(c);
    }

    public String stats() {
        return "created=%d idle=%d max=%d".formatted(created.get(), idle.size(), maxSize);
    }
}
