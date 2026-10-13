package com.handbook.microservices;

import com.handbook.microservices.resilience.bulkhead.Bulkhead;
import com.handbook.microservices.resilience.retry.HttpError;
import com.handbook.microservices.resilience.retry.Retry;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResilienceTest {

    private final Retry retry = new Retry(3, 10, 100, HttpError::isTransient, new Random(1), ms -> { });

    @Test
    void retriesTransientErrorsUpToMaxAttempts() {
        AtomicInteger calls = new AtomicInteger();
        assertThatThrownBy(() -> retry.call(() -> {
            calls.incrementAndGet();
            throw new HttpError(503, "down");
        })).isInstanceOf(HttpError.class);
        assertThat(calls).hasValue(3);
    }

    @Test
    void doesNotRetryClientErrors() {
        AtomicInteger calls = new AtomicInteger();
        assertThatThrownBy(() -> retry.call(() -> {
            calls.incrementAndGet();
            throw new HttpError(400, "bad");
        })).isInstanceOf(HttpError.class);
        assertThat(calls).hasValue(1);
    }

    @Test
    void bulkheadRejectsWhenFullAndReleasesPermits() {
        Bulkhead bulkhead = new Bulkhead("b", 1);
        bulkhead.call(() -> {
            assertThatThrownBy(() -> bulkhead.call(() -> "inner")).isInstanceOf(Bulkhead.FullException.class);
            return "outer";
        });
        assertThat(bulkhead.available()).isEqualTo(1);
    }
}
