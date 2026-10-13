package com.handbook.concurrency.rwlock;

import com.handbook.concurrency.rwlock.beds.BedBoard;
import com.handbook.concurrency.rwlock.custom.SimpleReadWriteLock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.assertj.core.api.Assertions.assertThat;

class ReadWriteLockTest {

    @Test
    @Timeout(5)
    void readersShareTheLock() throws Exception {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        CountDownLatch bothInside = new CountDownLatch(2);
        Runnable reader = () -> {
            lock.readLock().lock();
            try {
                bothInside.countDown();
                bothInside.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            } finally {
                lock.readLock().unlock();
            }
        };
        Thread a = Thread.ofVirtual().start(reader);
        Thread b = Thread.ofVirtual().start(reader);
        assertThat(bothInside.await(2, TimeUnit.SECONDS)).isTrue(); // both readers held it at once
        a.join();
        b.join();
    }

    @Test
    void upgradeIsNotPossible() throws Exception {
        assertThat(new BedBoard().tryUpgradeWhileReading()).isFalse();
    }

    @Test
    @Timeout(5)
    void customLockExcludesWritersFromReaders() throws Exception {
        SimpleReadWriteLock lock = new SimpleReadWriteLock();
        lock.lockRead();
        Thread writer = Thread.ofVirtual().start(() -> {
            try {
                lock.lockWrite();
                lock.unlockWrite();
            } catch (InterruptedException ignored) {
            }
        });
        writer.join(100);
        assertThat(writer.isAlive()).isTrue(); // blocked while a reader holds the lock
        lock.unlockRead();
        writer.join();
    }
}
