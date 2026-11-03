package com.handbook.enterprise.valueobject.healthcare;

import java.time.LocalDate;

public class StayPeriodDemo {

    public static void main(String[] args) {
        System.out.println("=== Healthcare: Bed Allocation with a StayPeriod Value Object ===");
        StayPeriod current = new StayPeriod(LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 14));
        StayPeriod nextPatient = new StayPeriod(LocalDate.of(2026, 3, 14), LocalDate.of(2026, 3, 16));
        System.out.println("Bed 7 current stay: " + current.nights() + " nights");
        System.out.println("Next patient admitted on discharge day overlaps? " + current.overlaps(nextPatient));

        StayPeriod extended = current.extendTo(LocalDate.of(2026, 3, 15));
        System.out.println("Doctor extends stay -> " + extended.nights() + " nights; now overlaps? " + extended.overlaps(nextPatient));
        System.out.println("Original value unchanged: " + current.nights() + " nights");
        try {
            new StayPeriod(LocalDate.of(2026, 3, 12), LocalDate.of(2026, 3, 12));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
