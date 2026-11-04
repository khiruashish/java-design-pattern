package com.handbook.testing.testdata.insurance;

import com.handbook.testing.testdata.insurance.Drivers.Driver;

import java.time.LocalDate;

import static com.handbook.testing.testdata.insurance.Drivers.aHighRiskDriver;
import static com.handbook.testing.testdata.insurance.Drivers.aYoungDriverInMumbai;
import static com.handbook.testing.testdata.insurance.Drivers.anExperiencedSafeDriver;

public class PersonasDemo {

    public static void main(String[] args) {
        System.out.println("=== Insurance: Premium Tests Written with Personas ===");
        LocalDate today = LocalDate.of(2026, 11, 1);
        for (Driver d : new Driver[] {
                aYoungDriverInMumbai().build(),
                anExperiencedSafeDriver().build(),
                aHighRiskDriver().build(),
                aYoungDriverInMumbai().claims(1).named("Young driver, 1 claim").build()}) { // persona + tweak
            System.out.printf("%-22s -> INR %,d%n", d.name(), PremiumCalculator.annualPremiumPaise(d, today) / 100);
        }
        System.out.println("Personas read like the underwriting guide; builders let a test tweak one detail.");
    }
}
