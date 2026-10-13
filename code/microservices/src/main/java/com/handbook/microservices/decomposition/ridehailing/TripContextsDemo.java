package com.handbook.microservices.decomposition.ridehailing;

import com.handbook.microservices.decomposition.ridehailing.TripContexts.Billing;
import com.handbook.microservices.decomposition.ridehailing.TripContexts.Dispatch;

public class TripContextsDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: One Word, Two Bounded Contexts ===");
        Dispatch dispatch = new Dispatch();
        Billing billing = new Billing();
        dispatch.onTripCompleted(billing::handle);

        Dispatch.Trip trip = new Dispatch.Trip("TRIP-77", "DRV-12", 12.9352, 77.6245, 4, "IN_PROGRESS");
        dispatch.complete(trip, "RIDER-5", 8.4, 23);
        System.out.println("Dispatch's Trip has GPS + ETA; Billing's Trip has fare components. Neither knows the other's model.");
    }
}
