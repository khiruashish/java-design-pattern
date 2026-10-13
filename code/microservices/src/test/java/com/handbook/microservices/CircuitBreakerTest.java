package com.handbook.microservices;

import com.handbook.microservices.circuitbreaker.core.CircuitBreaker;
import com.handbook.microservices.circuitbreaker.core.CircuitBreaker.Config;
import com.handbook.microservices.circuitbreaker.core.CircuitBreaker.State;
import com.handbook.microservices.support.FakeClock;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CircuitBreakerTest {

    private final FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
    private final CircuitBreaker breaker = new CircuitBreaker("t", new Config(4, 4, 50, Duration.ofSeconds(10), 1), clock);
    private final Supplier<String> failing = () -> {
        throw new IllegalStateException("boom");
    };

    @Test
    void opensAtFailureRateAndFailsFast() {
        breaker.call(() -> "ok", e -> "fb");
        breaker.call(() -> "ok", e -> "fb");
        breaker.call(failing, e -> "fb");
        assertThat(breaker.state()).isEqualTo(State.CLOSED); // only 3 calls, minimum is 4
        breaker.call(failing, e -> "fb");
        assertThat(breaker.state()).isEqualTo(State.OPEN);
        assertThatThrownBy(() -> breaker.call(() -> "ok")).isInstanceOf(CircuitBreaker.OpenException.class);
    }

    @Test
    void halfOpenTrialClosesOrReopens() {
        for (int i = 0; i < 4; i++) {
            breaker.call(failing, e -> "fb");
        }
        clock.advance(Duration.ofSeconds(10));
        assertThat(breaker.state()).isEqualTo(State.HALF_OPEN);
        breaker.call(failing, e -> "fb");
        assertThat(breaker.state()).isEqualTo(State.OPEN);
        clock.advance(Duration.ofSeconds(10));
        assertThat(breaker.call(() -> "ok", e -> "fb")).isEqualTo("ok");
        assertThat(breaker.state()).isEqualTo(State.CLOSED);
    }
}
