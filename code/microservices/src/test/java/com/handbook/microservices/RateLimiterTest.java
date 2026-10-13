package com.handbook.microservices;

import com.handbook.microservices.ratelimit.slidinglog.OtpRateLimiter;
import com.handbook.microservices.ratelimit.tokenbucket.TokenBucket;
import com.handbook.microservices.support.FakeClock;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterTest {

    private final FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");

    @Test
    void tokenBucketAllowsBurstThenRefills() {
        TokenBucket bucket = new TokenBucket(2, 1.0, clock);
        assertThat(bucket.tryAcquire()).isTrue();
        assertThat(bucket.tryAcquire()).isTrue();
        assertThat(bucket.tryAcquire()).isFalse();
        clock.advanceMillis(1_000);
        assertThat(bucket.tryAcquire()).isTrue();
    }

    @Test
    void slidingLogHasNoBoundaryBurst() {
        OtpRateLimiter limiter = new OtpRateLimiter(2, Duration.ofMinutes(1), clock);
        clock.advance(Duration.ofSeconds(50));
        assertThat(limiter.tryRequest("p")).isZero();
        assertThat(limiter.tryRequest("p")).isZero();
        clock.advance(Duration.ofSeconds(15)); // a new calendar minute, but within 60 s of both requests
        assertThat(limiter.tryRequest("p")).isPositive();
    }
}
