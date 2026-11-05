package com.handbook.testing.testdata.insurance;

import com.handbook.testing.testdata.insurance.Drivers.Driver;

import java.time.LocalDate;
import java.time.Period;

public final class PremiumCalculator {

    public static long annualPremiumPaise(Driver d, LocalDate today) {
        long premium = 900_000; // INR 9,000 base
        int age = Period.between(d.born(), today).getYears();
        if (age < 25) premium += 450_000;
        if (d.yearsLicensed() >= 20) premium -= 150_000;
        premium += d.claimsLast3Years() * 300_000L;
        if (d.city().equals("Mumbai") || d.city().equals("Delhi")) premium += 120_000;
        if (d.vehicle().equals("sports")) premium += 500_000;
        return premium;
    }

    private PremiumCalculator() {
    }
}
