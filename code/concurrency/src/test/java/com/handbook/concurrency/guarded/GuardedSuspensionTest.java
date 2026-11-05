package com.handbook.concurrency.guarded;

import com.handbook.concurrency.guarded.buffer.PlaybackBuffer;
import com.handbook.concurrency.guarded.delivery.PickupCounter;
import com.handbook.concurrency.guarded.settlement.SettlementJob;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class GuardedSuspensionTest {

    @Test
    void riderReceivesShelfOnceOrderIsPacked() throws Exception {
        PickupCounter counter = new PickupCounter();
        Thread kitchen = Thread.ofVirtual().start(() -> counter.markPacked("O", "A1"));
        assertThat(counter.awaitPickup("O", 5_000)).isEqualTo("A1");
        kitchen.join();
    }

    @Test
    void waitingTimesOut() throws Exception {
        assertThat(new PickupCounter().awaitPickup("never", 50)).isNull();
        assertThat(new PlaybackBuffer().awaitPlayable(1, 50)).isFalse();
    }

    @Test
    void concurrentTriggersBalk() throws Exception {
        CountDownLatch release = new CountDownLatch(1);
        SettlementJob job = new SettlementJob(release);
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Boolean> first = pool.submit(job::trigger);
            while (!job.isRunning()) {
                Thread.onSpinWait();
            }
            assertThat(pool.submit(job::trigger).get()).isFalse();
            release.countDown();
            assertThat(first.get()).isTrue();
        }
        assertThat(job.batchesSettled()).isEqualTo(1);
    }
}
