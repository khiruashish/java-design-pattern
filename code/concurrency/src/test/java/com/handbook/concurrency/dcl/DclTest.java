package com.handbook.concurrency.dcl;

import com.handbook.concurrency.dcl.lazy.Lazy;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DclTest {

    @RepeatedTest(5)
    void lazyInitializesExactlyOnceUnderContention() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        Lazy<Object> lazy = Lazy.of(() -> { calls.incrementAndGet(); return new Object(); });
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) {
                ex.submit(() -> { start.await(); return lazy.get(); });
            }
            start.countDown(); // release all threads at once
        }
        assertThat(calls).hasValue(1);
    }

    @Test
    void nullResultsAreRejected() {
        assertThatThrownBy(() -> Lazy.of(() -> null).get()).isInstanceOf(NullPointerException.class);
    }
}
