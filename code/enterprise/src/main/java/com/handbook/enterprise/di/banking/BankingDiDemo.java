package com.handbook.enterprise.di.banking;

import com.handbook.enterprise.di.banking.FixedDeposit.Deposit;
import com.handbook.enterprise.di.banking.FixedDeposit.InterestCalculator;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class BankingDiDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Injecting the Clock into Interest Accrual ===");
        Deposit fd = new Deposit("FD-77", 10_000_000, 725, LocalDate.of(2025, 1, 1)); // INR 1,00,000 at 7.25%
        ZoneId ist = ZoneId.of("Asia/Kolkata");

        for (String day : new String[] {"2025-01-31", "2025-07-01", "2026-01-01"}) {
            Clock fixed = Clock.fixed(Instant.parse(day + "T06:00:00Z"), ist);
            long paise = new InterestCalculator(fixed).accruedPaise(fd);
            System.out.printf("As of %s: accrued INR %d.%02d%n", day, paise / 100, paise % 100);
        }
        System.out.println("Month-end batch reruns for a past date give the same answer: the clock is an input.");
    }
}
