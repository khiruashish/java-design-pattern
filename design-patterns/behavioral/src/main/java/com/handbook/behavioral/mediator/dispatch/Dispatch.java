package com.handbook.behavioral.mediator.dispatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Mediator + colleagues. Riders and drivers never reference each other directly. */
public final class Dispatch {

    private Dispatch() {
    }

    public interface DispatchCenter { // (1)!
        void register(Driver driver);
        void requestRide(Rider rider);
        void tripFinished(Driver driver);
    }

    public static final class Driver {
        private final String name;
        private final int locationKm; // position on a 1-D road, to keep the demo simple
        private final DispatchCenter center;
        private boolean available = true;

        public Driver(String name, int locationKm, DispatchCenter center) {
            this.name = name;
            this.locationKm = locationKm;
            this.center = center;
            center.register(this);
        }

        void assign(Rider rider) {
            available = false;
            System.out.println("  " + name + ": heading to " + rider.name());
        }

        public void finishTrip() {
            available = true;
            center.tripFinished(this); // (2)!
        }

        public String name() { return name; }
    }

    public static final class Rider {
        private final String name;
        private final int locationKm;
        private final DispatchCenter center;

        public Rider(String name, int locationKm, DispatchCenter center) {
            this.name = name;
            this.locationKm = locationKm;
            this.center = center;
        }

        public void requestRide() {
            center.requestRide(this);
        }

        void notifyAssigned(Driver d, int etaMin) {
            System.out.println("  " + name + ": " + d.name() + " arriving in " + etaMin + " min");
        }

        void notifyWaiting() {
            System.out.println("  " + name + ": all drivers busy, you're in the queue");
        }

        public String name() { return name; }
    }

    public static final class CityDispatch implements DispatchCenter {
        private final List<Driver> drivers = new ArrayList<>();
        private final List<Rider> waiting = new ArrayList<>();

        public void register(Driver driver) {
            drivers.add(driver);
        }

        public void requestRide(Rider rider) {
            System.out.println(rider.name() + " requests a ride at km " + rider.locationKm);
            Optional<Driver> nearest = drivers.stream().filter(d -> d.available)
                    .min(Comparator.comparingInt(d -> Math.abs(d.locationKm - rider.locationKm))); // (3)!
            nearest.ifPresentOrElse(d -> match(d, rider), () -> {
                waiting.add(rider);
                rider.notifyWaiting();
            });
        }

        public void tripFinished(Driver driver) {
            System.out.println(driver.name() + " finished a trip");
            if (!waiting.isEmpty()) {
                match(driver, waiting.removeFirst());
            }
        }

        private void match(Driver d, Rider r) {
            int etaMin = Math.max(1, Math.abs(d.locationKm - r.locationKm) * 2);
            d.assign(r);
            r.notifyAssigned(d, etaMin);
        }
    }
}
