package com.handbook.creational.builder.travel;

import com.handbook.creational.builder.travel.FlightSearch.CabinClass;

import java.time.LocalDate;

public class TravelDemo {

    public static void main(String[] args) {
        System.out.println("=== Travel: Flight Search Queries ===");

        FlightSearch oneWay = FlightSearch.builder()
                .from("BLR").to("DEL")
                .departing(LocalDate.of(2026, 12, 20))
                .build();
        System.out.println(oneWay.summary());

        FlightSearch family = FlightSearch.builder()
                .from("BOM").to("SIN")
                .departing(LocalDate.of(2026, 12, 22))
                .returning(LocalDate.of(2027, 1, 2))
                .passengers(2, 2)
                .cabin(CabinClass.PREMIUM_ECONOMY)
                .nonStopOnly()
                .build();
        System.out.println(family.summary());

        try {
            FlightSearch.builder().from("DEL").to("LHR")
                    .departing(LocalDate.of(2027, 3, 10))
                    .returning(LocalDate.of(2027, 3, 1))
                    .build();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid search: " + e.getMessage());
        }
        // FlightSearch.builder().to("DEL")  <- does not compile: 'from' must come first
    }
}
