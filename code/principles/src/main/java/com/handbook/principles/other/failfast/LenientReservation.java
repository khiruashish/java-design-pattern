package com.handbook.principles.other.failfast;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** FAIL SLOW: accepts anything; the error surfaces much later as a wrong number, far from its cause. */
public class LenientReservation {
    private final LocalDate checkIn;
    private final LocalDate checkOut;

    public LenientReservation(LocalDate checkIn, LocalDate checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public long priceCents(long perNightCents) {
        return ChronoUnit.DAYS.between(checkIn, checkOut) * perNightCents;   // negative nights -> negative invoice
    }
}
