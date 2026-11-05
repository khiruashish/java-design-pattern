package com.handbook.testing.testdata.insurance;

import java.time.LocalDate;

/** Object Mother: named, realistic personas for the domain, built on top of a builder. */
public final class Drivers {

    public record Driver(String name, LocalDate born, String city, int claimsLast3Years, int yearsLicensed, String vehicle) { }

    public static final class DriverBuilder {
        private String name = "Test Driver";
        private LocalDate born = LocalDate.of(1990, 1, 1);
        private String city = "Pune";
        private int claims = 0;
        private int yearsLicensed = 10;
        private String vehicle = "hatchback";

        public DriverBuilder born(LocalDate d) { born = d; return this; }
        public DriverBuilder city(String c) { city = c; return this; }
        public DriverBuilder claims(int n) { claims = n; return this; }
        public DriverBuilder yearsLicensed(int y) { yearsLicensed = y; return this; }
        public DriverBuilder vehicle(String v) { vehicle = v; return this; }
        public DriverBuilder named(String n) { name = n; return this; }
        public Driver build() { return new Driver(name, born, city, claims, yearsLicensed, vehicle); }
    }

    // ---- The mother: personas the business team actually talks about ----

    public static DriverBuilder aYoungDriverInMumbai() { // (1)!
        return new DriverBuilder().named("Young driver").born(LocalDate.of(2005, 6, 1)).city("Mumbai").yearsLicensed(1);
    }

    public static DriverBuilder anExperiencedSafeDriver() {
        return new DriverBuilder().named("Safe driver").born(LocalDate.of(1978, 3, 9)).yearsLicensed(25);
    }

    public static DriverBuilder aHighRiskDriver() {
        return new DriverBuilder().named("High-risk driver").claims(3).vehicle("sports");
    }

    private Drivers() {
    }
}
