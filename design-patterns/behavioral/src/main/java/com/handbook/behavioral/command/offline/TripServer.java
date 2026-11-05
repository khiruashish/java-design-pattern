package com.handbook.behavioral.command.offline;

import com.handbook.behavioral.command.offline.DriverAction.*;

import java.util.HashSet;
import java.util.Set;

/** Receiver on the server: applies actions idempotently using the action id. */
public class TripServer {

    private final Set<String> seen = new HashSet<>();

    public void apply(DriverAction action) {
        if (!seen.add(action.id())) {
            System.out.println("  server: ignored duplicate " + action.id());
            return;
        }
        String what = switch (action) {
            case AcceptTrip a -> "trip " + a.tripId() + " accepted";
            case ArrivedAtPickup a -> "driver at pickup for " + a.tripId();
            case StartTrip s -> "trip " + s.tripId() + " started (OTP " + s.otp() + " verified)";
            case EndTrip e -> "trip " + e.tripId() + " ended, " + e.km() + " km, fare INR " + Math.round(50 + 14 * e.km());
        };
        System.out.println("  server @" + action.at() + ": " + what);
    }
}
