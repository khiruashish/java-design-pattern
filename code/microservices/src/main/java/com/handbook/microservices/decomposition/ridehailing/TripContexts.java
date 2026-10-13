package com.handbook.microservices.decomposition.ridehailing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The word "Trip" means different things in two bounded contexts. Each context has its OWN model;
 * they integrate through a published event, not a shared Trip class or shared table.
 */
public final class TripContexts {

    private TripContexts() {
    }

    // ---------- Dispatch context: moving cars around the city ----------
    public static final class Dispatch {
        public record Trip(String id, String driverId, double pickupLat, double pickupLng, int etaMinutes, String status) { // (1)!
        }

        /** The public contract Dispatch publishes. Deliberately small and stable. */
        public record TripCompleted(String tripId, String riderId, double distanceKm, int durationMin, String city) { // (2)!
        }

        private final List<Consumer<TripCompleted>> listeners = new ArrayList<>();

        public void onTripCompleted(Consumer<TripCompleted> l) {
            listeners.add(l);
        }

        public void complete(Trip trip, String riderId, double km, int minutes) {
            System.out.println("  [dispatch] " + trip.id() + " completed by driver " + trip.driverId() + " (ETA was " + trip.etaMinutes() + " min)");
            listeners.forEach(l -> l.accept(new TripCompleted(trip.id(), riderId, km, minutes, "Bengaluru")));
        }
    }

    // ---------- Billing context: charging riders ----------
    public static final class Billing {
        public record Trip(String id, String riderId, int baseFare, int distanceFare, int timeFare, int surge, int totalInr) { // (3)!
        }

        private final List<Trip> invoiced = new ArrayList<>();

        public void handle(Dispatch.TripCompleted e) { // translate THEIR language into OURS
            int base = 50;
            int distance = (int) Math.round(e.distanceKm() * 14);
            int time = e.durationMin() * 2;
            int surge = e.city().equals("Bengaluru") ? 20 : 0;
            Trip t = new Trip(e.tripId(), e.riderId(), base, distance, time, surge, base + distance + time + surge);
            invoiced.add(t);
            System.out.println("  [billing] invoice " + t);
        }

        public List<Trip> invoiced() {
            return invoiced;
        }
    }
}
