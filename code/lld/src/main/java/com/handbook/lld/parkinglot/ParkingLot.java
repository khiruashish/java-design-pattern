package com.handbook.lld.parkinglot;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Parking lot LLD. Patterns: Strategy (spot allocation, pricing), Factory-style creation of spots
 * per floor, and a small state model per spot (free/occupied).
 */
public final class ParkingLot {

    public enum VehicleType { BIKE, CAR, TRUCK }

    public enum SpotSize {
        SMALL, MEDIUM, LARGE;

        boolean fits(VehicleType v) {
            return switch (v) {
                case BIKE -> true;
                case CAR -> this != SMALL;
                case TRUCK -> this == LARGE;
            };
        }
    }

    public record Vehicle(String plate, VehicleType type) {
    }

    public static final class Spot {
        final String id;
        final int floor;
        final SpotSize size;
        Vehicle parked;

        Spot(String id, int floor, SpotSize size) {
            this.id = id;
            this.floor = floor;
            this.size = size;
        }

        boolean isFree() {
            return parked == null;
        }
    }

    public record Ticket(String id, Vehicle vehicle, String spotId, Instant entry) {
    }

    public record Receipt(String ticketId, String plate, Duration parked, long feePaise) {
    }

    /** Strategy: which free spot to give a vehicle. */
    public interface AllocationStrategy {
        Optional<Spot> choose(List<Spot> spots, Vehicle vehicle);
    }

    /** Strategy: how much to charge. */
    public interface PricingStrategy {
        long feePaise(VehicleType type, Duration parked);
    }

    /** Nearest floor first, and the smallest spot that fits (keeps large spots for trucks). */
    public static AllocationStrategy nearestSmallestFit() {
        return (spots, v) -> spots.stream()
                .filter(Spot::isFree)
                .filter(s -> s.size.fits(v.type()))
                .min((a, b) -> a.floor != b.floor ? Integer.compare(a.floor, b.floor) : a.size.compareTo(b.size));
    }

    /** First 30 minutes free, then a per-hour rate by vehicle type, rounded up to whole hours. */
    public static PricingStrategy hourly(Map<VehicleType, Long> ratePerHourPaise) {
        return (type, parked) -> {
            if (parked.compareTo(Duration.ofMinutes(30)) <= 0) {
                return 0;
            }
            long hours = (parked.toMinutes() + 59) / 60;
            return hours * ratePerHourPaise.get(type);
        };
    }

    private final List<Spot> spots = new ArrayList<>();
    private final Map<String, Ticket> active = new HashMap<>();
    private final Map<String, Spot> spotById = new HashMap<>();
    private final AllocationStrategy allocation;
    private final PricingStrategy pricing;
    private int nextTicket = 1;

    public ParkingLot(AllocationStrategy allocation, PricingStrategy pricing) {
        this.allocation = allocation;
        this.pricing = pricing;
    }

    public ParkingLot addFloor(int floor, int small, int medium, int large) {
        add(floor, SpotSize.SMALL, small);
        add(floor, SpotSize.MEDIUM, medium);
        add(floor, SpotSize.LARGE, large);
        return this;
    }

    private void add(int floor, SpotSize size, int count) {
        for (int i = 1; i <= count; i++) {
            Spot s = new Spot("F" + floor + "-" + size.name().charAt(0) + i, floor, size);
            spots.add(s);
            spotById.put(s.id, s);
        }
    }

    public synchronized Optional<Ticket> park(Vehicle vehicle, Instant at) {
        return allocation.choose(spots, vehicle).map(spot -> {
            spot.parked = vehicle;
            Ticket t = new Ticket("T-" + nextTicket++, vehicle, spot.id, at);
            active.put(t.id(), t);
            return t;
        });
    }

    public synchronized Receipt exit(String ticketId, Instant at) {
        Ticket t = active.remove(ticketId);
        if (t == null) {
            throw new IllegalArgumentException("unknown or already used ticket " + ticketId);
        }
        spotById.get(t.spotId()).parked = null;
        Duration parked = Duration.between(t.entry(), at);
        return new Receipt(t.id(), t.vehicle().plate(), parked, pricing.feePaise(t.vehicle().type(), parked));
    }

    public synchronized long freeSpots(VehicleType type) {
        return spots.stream().filter(Spot::isFree).filter(s -> s.size.fits(type)).count();
    }
}
