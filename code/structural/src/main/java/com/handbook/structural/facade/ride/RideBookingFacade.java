package com.handbook.structural.facade.ride;

/** Challenge solution: one call to book a ride, hiding pricing, matching and payment holds. */
public class RideBookingFacade {

    static class SurgePricing {
        int fareInr(int km, boolean peak) {
            int base = 50 + 14 * km;
            return peak ? base * 3 / 2 : base;
        }
    }

    static class DriverMatcher {
        String nearestDriver(String pickup) {
            return pickup.equals("Airport T2") ? null : "Driver Ramesh (KA-01-AB-1234), 4 min away";
        }
    }

    static class PaymentHold {
        String hold(String rider, int amount) {
            return "HOLD-" + rider + "-" + amount;
        }

        void releaseHold(String holdId) {
            System.out.println("  released " + holdId);
        }
    }

    private final SurgePricing pricing = new SurgePricing();
    private final DriverMatcher matcher = new DriverMatcher();
    private final PaymentHold payments = new PaymentHold();

    public String book(String rider, String pickup, int km, boolean peak) {
        int fare = pricing.fareInr(km, peak);
        String hold = payments.hold(rider, fare);
        String driver = matcher.nearestDriver(pickup);
        if (driver == null) {
            payments.releaseHold(hold);
            return "No drivers near " + pickup + ", you were not charged";
        }
        return "Booked: " + driver + ", fare INR " + fare + (peak ? " (1.5x surge)" : "");
    }
}
