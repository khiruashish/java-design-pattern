package com.handbook.microservices;

import com.handbook.microservices.leaderelection.fencing.FencedStorage;
import com.handbook.microservices.leaderelection.lease.DbLease;
import com.handbook.microservices.leaderelection.sequential.SequentialElection;
import com.handbook.microservices.support.FakeClock;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class LeaderElectionTest {

    @Test
    void onlyOneHolderUntilTheLeaseExpires() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        DbLease lease = new DbLease(clock, Duration.ofSeconds(10));
        assertThat(lease.tryAcquireOrRenew("a")).isTrue();
        assertThat(lease.tryAcquireOrRenew("b")).isFalse();
        clock.advance(Duration.ofSeconds(11));
        assertThat(lease.tryAcquireOrRenew("b")).isTrue();
        assertThat(lease.tryAcquireOrRenew("a")).isFalse();
    }

    @Test
    void staleTokensAreFencedOff() {
        FencedStorage s = new FencedStorage();
        assertThat(s.write(5, "x")).isTrue();
        assertThat(s.write(6, "y")).isTrue();
        assertThat(s.write(5, "z")).isFalse();
    }

    @Test
    void lowestSequenceLeadsAndSuccessionIsOrdered() {
        SequentialElection e = new SequentialElection();
        e.join("a");
        e.join("b");
        e.sessionExpired("a");
        assertThat(e.leader()).contains("b");
    }
}
