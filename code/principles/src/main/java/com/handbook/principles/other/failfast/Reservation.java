package com.handbook.principles.other.failfast;

import java.time.LocalDate;
import java.util.Objects;

/** FAIL FAST: an invalid Reservation can never exist, so nothing downstream has to re-check it. */
public record Reservation(String guest, LocalDate checkIn, LocalDate checkOut, int guests) {
    public Reservation {
        Objects.requireNonNull(guest, "guest");
        Objects.requireNonNull(checkIn, "checkIn");
        Objects.requireNonNull(checkOut, "checkOut");
        if (guest.isBlank()) throw new IllegalArgumentException("guest must not be blank");
        if (!checkOut.isAfter(checkIn)) throw new IllegalArgumentException("checkOut " + checkOut + " must be after checkIn " + checkIn);
        if (guests < 1 || guests > 8) throw new IllegalArgumentException("guests must be 1..8 but was " + guests);
    }

    public long nights() { return checkIn.until(checkOut).getDays(); }
}
