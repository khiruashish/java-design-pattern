package com.handbook.behavioral.command.offline;

import com.handbook.behavioral.command.offline.DriverAction.*;

public class OfflineDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: Driver App Through a Tunnel ===");
        TripServer server = new TripServer();
        DriverApp app = new DriverApp(server::apply);

        app.perform(new AcceptTrip("A1", "09:00", "TRIP-77"));
        app.perform(new ArrivedAtPickup("A2", "09:07", "TRIP-77"));
        app.setOnline(false);
        app.perform(new StartTrip("A3", "09:09", "TRIP-77", "4821"));
        app.perform(new EndTrip("A4", "09:31", "TRIP-77", 8.4));
        app.setOnline(true);
        server.apply(new EndTrip("A4", "09:31", "TRIP-77", 8.4)); // a flaky network re-sends A4
    }
}
