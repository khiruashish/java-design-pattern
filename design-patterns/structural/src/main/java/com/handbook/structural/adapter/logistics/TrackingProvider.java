package com.handbook.structural.adapter.logistics;

/** Target interface used by the order-tracking page. */
public interface TrackingProvider {

    enum Status { PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED }

    record Tracking(String carrier, String trackingId, Status status, String location) {
    }

    Tracking track(String trackingId);
}
