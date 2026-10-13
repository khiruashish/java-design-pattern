package com.handbook.lld.parkinglot;

import com.handbook.lld.parkinglot.ParkingLot.Vehicle;
import com.handbook.lld.parkinglot.ParkingLot.VehicleType;

import java.time.Instant;
import java.util.Map;

public class ParkingLotDemo {

    public static void main(String[] args) {
        var lot = new ParkingLot(ParkingLot.nearestSmallestFit(), ParkingLot.hourly(Map.of(
                VehicleType.BIKE, 1_000L, VehicleType.CAR, 4_000L, VehicleType.TRUCK, 10_000L)))
                .addFloor(0, 1, 1, 0)
                .addFloor(1, 0, 1, 1);
        Instant nine = Instant.parse("2026-10-14T09:00:00Z");

        System.out.println("=== LLD: Parking lot ===");
        var bike = lot.park(new Vehicle("KA01-B-1", VehicleType.BIKE), nine).orElseThrow();
        var car1 = lot.park(new Vehicle("KA01-C-1", VehicleType.CAR), nine).orElseThrow();
        var car2 = lot.park(new Vehicle("KA01-C-2", VehicleType.CAR), nine).orElseThrow();
        var truck = lot.park(new Vehicle("KA01-T-1", VehicleType.TRUCK), nine.plusSeconds(600));
        System.out.println("bike  -> " + bike.spotId() + " (smallest fitting spot on the nearest floor)");
        System.out.println("car 1 -> " + car1.spotId());
        System.out.println("car 2 -> " + car2.spotId());
        System.out.println("truck -> " + truck.map(t -> t.spotId()).orElse("FULL"));
        System.out.println("free spots for cars now: " + lot.freeSpots(VehicleType.CAR));
        System.out.println("another car -> " + lot.park(new Vehicle("KA01-C-3", VehicleType.CAR), nine)
                .map(t -> t.spotId()).orElse("FULL"));

        System.out.println(lot.exit(bike.id(), nine.plusSeconds(20 * 60)) + "  (first 30 min free)");
        System.out.println(lot.exit(car1.id(), nine.plusSeconds(2 * 3600 + 5 * 60)) + "  (3 started hours)");
        try {
            lot.exit(car1.id(), nine.plusSeconds(3 * 3600));
        } catch (IllegalArgumentException e) {
            System.out.println("re-using a ticket: " + e.getMessage());
        }
    }
}
