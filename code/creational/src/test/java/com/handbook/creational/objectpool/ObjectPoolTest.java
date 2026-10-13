package com.handbook.creational.objectpool;

import com.handbook.creational.objectpool.banking.ConnectionPool;
import com.handbook.creational.objectpool.generic.ObjectPool;
import com.handbook.creational.objectpool.streaming.TranscoderPool;
import com.handbook.creational.objectpool.travel.GdsSessionPool;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjectPoolTest {

    @Test
    void connectionPoolReusesAndLimits() throws Exception {
        ConnectionPool pool = new ConnectionPool(1);
        int firstId;
        try (var lease = pool.acquire(10)) {
            firstId = lease.connection().id();
        }
        try (var lease = pool.acquire(10)) {
            assertThat(lease.connection().id()).isEqualTo(firstId);
            assertThatThrownBy(() -> pool.acquire(10)).isInstanceOf(TimeoutException.class);
        }
    }

    @Test
    void leaseCanBeClosedTwiceSafely() throws Exception {
        ConnectionPool pool = new ConnectionPool(1);
        var lease = pool.acquire(10);
        lease.close();
        lease.close();
        assertThat(pool.stats()).contains("idle=1");
    }

    @Test
    void transcoderPoolNeverExceedsItsSize() {
        TranscoderPool pool = new TranscoderPool(2);
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) {
                ex.submit(() -> { pool.run("v"); return null; });
            }
        }
        assertThat(pool.totalJobs()).isEqualTo(10);
        assertThat(pool.peakInUse()).isLessThanOrEqualTo(2);
    }

    @Test
    void gdsPoolRejectsBeyondLicences() {
        GdsSessionPool pool = new GdsSessionPool(1, 10);
        assertThatThrownBy(() -> pool.withSession(a -> pool.withSession(b -> "x")))
                .hasMessageContaining("licences");
    }

    @Test
    void genericPoolValidatesAndResets() throws Exception {
        AtomicInteger created = new AtomicInteger();
        ObjectPool<StringBuilder> pool = new ObjectPool<>(1,
                () -> { created.incrementAndGet(); return new StringBuilder(); },
                sb -> !sb.toString().equals("broken"), sb -> {});
        StringBuilder sb = pool.borrow(10, TimeUnit.MILLISECONDS);
        sb.append("broken");
        pool.giveBack(sb);
        pool.borrow(10, TimeUnit.MILLISECONDS); // broken one is discarded
        assertThat(created).hasValue(2);
    }
}
