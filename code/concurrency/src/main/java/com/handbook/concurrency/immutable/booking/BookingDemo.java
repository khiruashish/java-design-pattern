package com.handbook.concurrency.immutable.booking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Challenge: find the leaks in MutableBooking, then fix them in Booking. */
public class BookingDemo {

    /** BEFORE: looks immutable (final fields, no setters) but leaks mutable state. */
    static final class MutableBooking {
        private final Date checkIn;
        private final List<String> guests;

        MutableBooking(Date checkIn, List<String> guests) {
            this.checkIn = checkIn;      // stores the caller's object
            this.guests = guests;        // stores the caller's list
        }

        Date checkIn() { return checkIn; }           // hands out the internal object

        List<String> guests() { return guests; }     // hands out the internal list
    }

    /** AFTER: an immutable record with immutable types and defensive copies. */
    record Booking(LocalDate checkIn, List<String> guests) {
        Booking {
            guests = List.copyOf(guests); // copy in; List.copyOf is also unmodifiable for callers
        }
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        Date date = new Date(126, 11, 27); // 27 Dec 2026, java.util.Date is mutable
        List<String> names = new ArrayList<>(List.of("Asha"));
        MutableBooking leaky = new MutableBooking(date, names);
        date.setDate(31);                  // caller changes its Date...
        leaky.guests().add("Intruder");    // ...or anyone changes the returned list
        System.out.println("Leaky booking   : check-in day " + leaky.checkIn().getDate() + ", guests " + leaky.guests());

        List<String> names2 = new ArrayList<>(List.of("Asha"));
        Booking safe = new Booking(LocalDate.of(2026, 12, 27), names2);
        names2.add("Intruder");
        try {
            safe.guests().add("Intruder");
        } catch (UnsupportedOperationException e) {
            System.out.println("Fixed booking   : guests list is unmodifiable");
        }
        System.out.println("Fixed booking   : " + safe);
    }
}
