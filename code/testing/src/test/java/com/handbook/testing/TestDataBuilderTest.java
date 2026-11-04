package com.handbook.testing;

import com.handbook.testing.testdata.insurance.PremiumCalculator;
import com.handbook.testing.testdata.ledger.RandomTransfers;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.handbook.testing.testdata.grocery.OrderBuilder.anOrder;
import static com.handbook.testing.testdata.insurance.Drivers.aHighRiskDriver;
import static com.handbook.testing.testdata.insurance.Drivers.anExperiencedSafeDriver;
import static org.assertj.core.api.Assertions.assertThat;

class TestDataBuilderTest {

    @Test
    void freeDeliveryAboveThreshold() {
        assertThat(anOrder().withSubtotal(49_900).build().deliveryFeePaise()).isZero();
    }

    @Test
    void expressAddsThirtyRupees() {
        assertThat(anOrder().withSubtotal(49_900).express().build().deliveryFeePaise()).isEqualTo(3_000);
    }

    @Test
    void firstOrderIsAlwaysFree() {
        assertThat(anOrder().express().firstOrder().build().deliveryFeePaise()).isZero();
    }

    @Test
    void highRiskPaysMoreThanSafe() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        assertThat(PremiumCalculator.annualPremiumPaise(aHighRiskDriver().build(), today))
                .isGreaterThan(PremiumCalculator.annualPremiumPaise(anExperiencedSafeDriver().build(), today));
    }

    @Test
    void generatedDataIsReproducibleAndValid() {
        var a = RandomTransfers.generate(42, 500, List.of("X", "Y", "Z"));
        assertThat(a).isEqualTo(RandomTransfers.generate(42, 500, List.of("X", "Y", "Z")));
        assertThat(a).allSatisfy(t -> assertThat(t.from()).isNotEqualTo(t.to()));
    }
}
