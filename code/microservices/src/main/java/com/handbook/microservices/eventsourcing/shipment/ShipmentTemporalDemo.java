package com.handbook.microservices.eventsourcing.shipment;

import com.handbook.microservices.eventsourcing.shipment.ShipmentHistory.Scanned;

import java.time.Instant;
import java.util.List;

public class ShipmentTemporalDemo {

    public static void main(String[] args) {
        var history = new ShipmentHistory(List.of(
                new Scanned(Instant.parse("2026-10-12T06:10:00Z"), "Mumbai hub", "PICKED_UP"),
                new Scanned(Instant.parse("2026-10-12T13:45:00Z"), "Pune sort centre", "IN_TRANSIT"),
                new Scanned(Instant.parse("2026-10-12T18:20:00Z"), "Pune sort centre", "DELAYED_WEATHER"),
                new Scanned(Instant.parse("2026-10-13T09:05:00Z"), "Bengaluru hub", "IN_TRANSIT"),
                new Scanned(Instant.parse("2026-10-13T15:30:00Z"), "Koramangala", "DELIVERED")));

        System.out.println("=== Logistics: temporal queries on a shipment ===");
        System.out.println("Now                         : " + history.current());
        System.out.println("At 2026-10-12 14:00 UTC     : " + history.stateAt(Instant.parse("2026-10-12T14:00:00Z")));
        System.out.println("At 2026-10-12 23:59 UTC     : " + history.stateAt(Instant.parse("2026-10-12T23:59:00Z")));
        System.out.println("Customer says: \"the delay was the courier's fault\" -> the history shows a weather delay at Pune, 18:20.");
    }
}
