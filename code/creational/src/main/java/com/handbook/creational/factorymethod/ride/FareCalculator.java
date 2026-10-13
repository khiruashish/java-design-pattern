package com.handbook.creational.factorymethod.ride;

public interface FareCalculator {

    int fareInr(int distanceKm);

    /** Factory method on the interface itself, like {@code List.of(...)}. */
    static FareCalculator of(RideType type) {
        return switch (type) {
            case AUTO -> km -> 30 + 12 * km;
            case MINI -> km -> 50 + 14 * km;
            case SEDAN -> km -> Math.max(150, 80 + 18 * km); // minimum fare
        };
    }
}
