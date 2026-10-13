package com.handbook.creational.factorymethod.logistics;

/**
 * Creator. Holds the shared planning workflow and defers the choice of
 * transport to subclasses through the factory method.
 */
public abstract class LogisticsPlanner {

    protected abstract Transport createTransport(); // (1)!

    public final String plan(String shipmentId, String route, int distanceKm) { // (2)!
        Transport transport = createTransport();
        int cost = transport.costPerKmInr() * distanceKm;
        int etaHours = Math.ceilDiv(distanceKm, transport.speedKmph());
        return "%s %-20s via %-5s | %4d km | cost INR %6d | ETA %2d h"
                .formatted(shipmentId, route, transport.name(), distanceKm, cost, etaHours);
    }
}
