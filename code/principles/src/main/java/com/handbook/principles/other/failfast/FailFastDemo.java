package com.handbook.principles.other.failfast;

import java.time.LocalDate;

public class FailFastDemo {
    public static void main(String[] args) {
        LocalDate in = LocalDate.of(2025, 3, 10);
        LocalDate out = LocalDate.of(2025, 3, 7);       // typo: before check-in

        System.out.println("fail slow: price = " + new LenientReservation(in, out).priceCents(5_000) + " cents  <-- refund by accident");
        try {
            new Reservation("Ana", in, out, 2);
        } catch (IllegalArgumentException e) {
            System.out.println("fail fast: " + e.getMessage());
        }
        System.out.println("valid    : " + new Reservation("Ana", in, in.plusDays(3), 2).nights() + " nights");
    }
}
