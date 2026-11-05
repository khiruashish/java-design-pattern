package com.handbook.creational.builder.travel;

import java.time.LocalDate;

/**
 * Flight search query built with a <b>step builder</b>: the compiler forces
 * from -> to -> departing, and only then exposes the optional settings and build().
 */
public record FlightSearch(String from, String to, LocalDate departing, LocalDate returning,
                           int adults, int children, CabinClass cabin, boolean nonStopOnly) {

    public enum CabinClass { ECONOMY, PREMIUM_ECONOMY, BUSINESS }

    public FlightSearch { // (1)!
        if (from.equals(to)) {
            throw new IllegalArgumentException("Origin and destination must differ");
        }
        if (returning != null && returning.isBefore(departing)) {
            throw new IllegalArgumentException("Return date is before departure");
        }
        if (adults < 1 || adults + children > 9) {
            throw new IllegalArgumentException("1-9 travellers, at least one adult");
        }
    }

    // ---- the steps: each interface exposes exactly one next move ----
    public interface FromStep { ToStep from(String airport); } // (2)!

    public interface ToStep { DepartingStep to(String airport); }

    public interface DepartingStep { OptionalStep departing(LocalDate date); }

    public interface OptionalStep { // (3)!
        OptionalStep returning(LocalDate date);
        OptionalStep passengers(int adults, int children);
        OptionalStep cabin(CabinClass cabin);
        OptionalStep nonStopOnly();
        FlightSearch build();
    }

    public static FromStep builder() {
        return new Steps();
    }

    private static final class Steps implements FromStep, ToStep, DepartingStep, OptionalStep { // (4)!
        private String from;
        private String to;
        private LocalDate departing;
        private LocalDate returning;
        private int adults = 1;
        private int children;
        private CabinClass cabin = CabinClass.ECONOMY;
        private boolean nonStopOnly;

        public ToStep from(String airport) { this.from = airport; return this; }
        public DepartingStep to(String airport) { this.to = airport; return this; }
        public OptionalStep departing(LocalDate date) { this.departing = date; return this; }
        public OptionalStep returning(LocalDate date) { this.returning = date; return this; }
        public OptionalStep passengers(int a, int c) { this.adults = a; this.children = c; return this; }
        public OptionalStep cabin(CabinClass cabin) { this.cabin = cabin; return this; }
        public OptionalStep nonStopOnly() { this.nonStopOnly = true; return this; }

        public FlightSearch build() {
            return new FlightSearch(from, to, departing, returning, adults, children, cabin, nonStopOnly);
        }
    }

    public String summary() {
        return "%s->%s %s%s | %d adult(s), %d child(ren) | %s%s".formatted(
                from, to, departing, returning == null ? " one-way" : " return " + returning,
                adults, children, cabin, nonStopOnly ? " | non-stop" : "");
    }
}
