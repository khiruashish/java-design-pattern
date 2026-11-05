package com.handbook.behavioral.command.offline;

/** Commands recorded on the phone while offline. Each carries an id and the time it happened. */
public sealed interface DriverAction {

    String id();

    String at();

    record AcceptTrip(String id, String at, String tripId) implements DriverAction {
    }

    record ArrivedAtPickup(String id, String at, String tripId) implements DriverAction {
    }

    record StartTrip(String id, String at, String tripId, String otp) implements DriverAction {
    }

    record EndTrip(String id, String at, String tripId, double km) implements DriverAction {
    }
}
