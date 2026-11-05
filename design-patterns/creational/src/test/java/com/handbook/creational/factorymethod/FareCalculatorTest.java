package com.handbook.creational.factorymethod;

import com.handbook.creational.factorymethod.ride.FareCalculator;
import com.handbook.creational.factorymethod.ride.RideType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FareCalculatorTest {

    @Test
    void faresPerRideType() {
        assertThat(FareCalculator.of(RideType.AUTO).fareInr(10)).isEqualTo(150);
        assertThat(FareCalculator.of(RideType.MINI).fareInr(10)).isEqualTo(190);
        assertThat(FareCalculator.of(RideType.SEDAN).fareInr(3)).isEqualTo(150); // minimum fare
    }
}
