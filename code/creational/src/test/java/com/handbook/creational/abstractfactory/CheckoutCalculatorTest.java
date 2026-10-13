package com.handbook.creational.abstractfactory;

import com.handbook.creational.abstractfactory.region.CheckoutCalculator;
import com.handbook.creational.abstractfactory.region.GermanyRegion;
import com.handbook.creational.abstractfactory.region.IndiaRegion;
import com.handbook.creational.abstractfactory.region.JapanRegion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CheckoutCalculatorTest {

    @Test
    void indiaAppliesGstAndSmallOrderFee() {
        assertThat(new CheckoutCalculator(new IndiaRegion()).summary(150_00))
                .contains("GST 5% INR 7.50", "delivery INR 25.00", "= INR 182.50");
    }

    @Test
    void germanyUsesEuroFormatAndReducedVat() {
        assertThat(new CheckoutCalculator(new GermanyRegion()).summary(50_00))
                .contains("MwSt 7% 3,50 EUR", "= 53,50 EUR");
    }

    @Test
    void minimumOrderIsEnforcedPerRegion() {
        assertThat(new CheckoutCalculator(new IndiaRegion()).summary(50_00))
                .isEqualTo("Minimum order is INR 99.00");
    }

    @Test
    void japanFamilyPlugsInWithoutChangingTheClient() {
        assertThat(new CheckoutCalculator(new JapanRegion()).summary(3_000))
                .contains("JPY 240", "= JPY 3,240");
    }
}
