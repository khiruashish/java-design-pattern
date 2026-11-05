package com.handbook.enterprise.valueobject.healthcare;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** A half-open date range [admitted, discharged). Rules about stays live here, not in services. */
public record StayPeriod(LocalDate admitted, LocalDate discharged) {

    public StayPeriod {
        Objects.requireNonNull(admitted);
        Objects.requireNonNull(discharged);
        if (!discharged.isAfter(admitted)) {
            throw new IllegalArgumentException("discharge " + discharged + " must be after admission " + admitted);
        }
    }

    public long nights() {
        return ChronoUnit.DAYS.between(admitted, discharged);
    }

    public boolean overlaps(StayPeriod other) { // (1)!
        return admitted.isBefore(other.discharged) && other.admitted.isBefore(discharged);
    }

    public StayPeriod extendTo(LocalDate newDischarge) { // (2)!
        return new StayPeriod(admitted, newDischarge);
    }
}
