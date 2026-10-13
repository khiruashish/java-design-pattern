package com.handbook.concurrency.threadpool;

import com.handbook.concurrency.threadpool.custom.SimpleThreadPool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThreadPoolTest {

    @Test
    @Timeout(5)
    void saturatedPoolRejectsWithAbortPolicy() throws Exception {
        CountDownLatch block = new CountDownLatch(1);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        pool.execute(() -> { try { block.await(); } catch (InterruptedException ignored) { } });
        pool.execute(() -> { });
        assertThatThrownBy(() -> pool.execute(() -> { })).isInstanceOf(RejectedExecutionException.class);
        block.countDown();
        pool.shutdown();
        assertThat(pool.awaitTermination(2, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    @Timeout(5)
    void callerRunsPolicyRunsTaskOnSubmittingThread() throws Exception {
        CountDownLatch block = new CountDownLatch(1);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1),
                new ThreadPoolExecutor.CallerRunsPolicy());
        pool.execute(() -> { try { block.await(); } catch (InterruptedException ignored) { } });
        pool.execute(() -> { });
        String[] ranOn = new String[1];
        pool.execute(() -> ranOn[0] = Thread.currentThread().getName());
        assertThat(ranOn[0]).isEqualTo(Thread.currentThread().getName());
        block.countDown();
        pool.shutdown();
    }

    @Test
    @Timeout(5)
    void simplePoolCompletesQueuedWorkBeforeShutdown() throws Exception {
        SimpleThreadPool pool = new SimpleThreadPool(2, 100);
        AtomicInteger n = new AtomicInteger();
        for (int i = 0; i < 100; i++) {
            pool.execute(n::incrementAndGet);
        }
        pool.shutdownAndWait();
        assertThat(n).hasValue(100);
    }
}
