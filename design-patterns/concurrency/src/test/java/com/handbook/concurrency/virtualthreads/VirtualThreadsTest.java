package com.handbook.concurrency.virtualthreads;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class VirtualThreadsTest {

    @Test
    void virtualThreadsAreDaemonAndVirtual() throws Exception {
        Thread t = Thread.ofVirtual().name("vt-1").start(() -> { });
        t.join();
        assertThat(t.isVirtual()).isTrue();
        assertThat(t.isDaemon()).isTrue();
        assertThat(t.getName()).isEqualTo("vt-1");
    }

    @Test
    @Timeout(10)
    void tenThousandBlockingTasksFinishQuickly() {
        AtomicInteger done = new AtomicInteger();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                ex.submit(() -> { Thread.sleep(200); done.incrementAndGet(); return null; });
            }
        }
        assertThat(done).hasValue(10_000);
    }
}
