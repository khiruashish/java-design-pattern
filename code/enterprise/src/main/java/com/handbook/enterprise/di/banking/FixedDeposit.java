package com.handbook.enterprise.di.banking;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Interest calculation with time injected instead of hidden inside LocalDate.now(). */
public final class FixedDeposit {

    public record Deposit(String id, long principalPaise, int rateBasisPoints, LocalDate openedOn) {
    }

    /** Hidden dependency: reads the system clock directly, so the result changes every day. */
    public static final class HardWiredInterest {
        public long accruedPaise(Deposit d) {
            long days = ChronoUnit.DAYS.between(d.openedOn(), LocalDate.now()); // (1)!
            return d.principalPaise() * d.rateBasisPoints() * days / (10_000L * 365);
        }
    }

    /** Explicit dependency: the clock is a constructor argument. */
    public static final class InterestCalculator {
        private final Clock clock;

        public InterestCalculator(Clock clock) { // (2)!
            this.clock = clock;
        }

        public long accruedPaise(Deposit d) {
            long days = ChronoUnit.DAYS.between(d.openedOn(), LocalDate.now(clock));
            return d.principalPaise() * d.rateBasisPoints() * days / (10_000L * 365);
        }
    }

    private FixedDeposit() {
    }
}
