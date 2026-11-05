package com.handbook.concurrency.immutable;

import com.handbook.concurrency.immutable.config.RateLimitConfig;
import com.handbook.concurrency.immutable.money.Money;
import com.handbook.concurrency.immutable.order.OrderSnapshot;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImmutableTest {

    @Test
    void allocationNeverLosesPaise() {
        for (int parts = 1; parts <= 7; parts++) {
            Money total = Money.inr("100.00").allocate(parts).stream().reduce(Money.inr("0"), Money::plus);
            assertThat(total).isEqualTo(Money.inr("100.00"));
        }
    }

    @Test
    void moneyIsAValue() {
        assertThat(Money.inr("5")).isEqualTo(Money.inr("5.00")); // scale normalized in the constructor
        assertThatThrownBy(() -> Money.inr("1").plus(new Money(java.math.BigDecimal.ONE, java.util.Currency.getInstance("USD"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void snapshotIsIsolatedFromCallerList() {
        List<OrderSnapshot.Line> lines = new ArrayList<>(List.of(new OrderSnapshot.Line("A", 1, 10)));
        OrderSnapshot s = new OrderSnapshot("O", Instant.EPOCH, lines);
        lines.clear();
        assertThat(s.totalInr()).isEqualTo(10);
    }

    @Test
    void invalidConfigIsRejectedAndOldSnapshotKept() {
        RateLimitConfig c = new RateLimitConfig();
        assertThatThrownBy(() -> c.update(10, 100)).isInstanceOf(IllegalArgumentException.class);
        assertThat(c.get().version()).isEqualTo(1);
    }
}
