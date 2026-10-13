package com.handbook.microservices.acl.logistics;

import java.util.Map;

/**
 * Each delivery partner reports status in its own vocabulary. One adapter per partner maps it to
 * OUR status model; unknown codes become UNKNOWN (and an alert) instead of crashing the pipeline.
 */
public final class CarrierStatusTranslator {

    public enum ShipmentStatus { PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, FAILED_ATTEMPT, UNKNOWN }

    public record CarrierEvent(String carrier, String awb, String rawStatus) {
    }

    public record TrackingUpdate(String trackingId, ShipmentStatus status) {
    }

    private static final Map<String, Map<String, ShipmentStatus>> MAPPINGS = Map.of( // (1)!
            "QuickDrop", Map.of("PU", ShipmentStatus.PICKED_UP, "IT", ShipmentStatus.IN_TRANSIT,
                    "OFD", ShipmentStatus.OUT_FOR_DELIVERY, "DL", ShipmentStatus.DELIVERED,
                    "NDR", ShipmentStatus.FAILED_ATTEMPT),
            "BlueKite", Map.of("1", ShipmentStatus.PICKED_UP, "2", ShipmentStatus.IN_TRANSIT,
                    "3", ShipmentStatus.OUT_FOR_DELIVERY, "4", ShipmentStatus.DELIVERED,
                    "9", ShipmentStatus.FAILED_ATTEMPT));

    public TrackingUpdate translate(CarrierEvent event) {
        ShipmentStatus status = MAPPINGS.getOrDefault(event.carrier(), Map.of())
                .getOrDefault(event.rawStatus().trim().toUpperCase(), ShipmentStatus.UNKNOWN); // (2)!
        if (status == ShipmentStatus.UNKNOWN) {
            System.out.println("    ALERT: unmapped status '" + event.rawStatus() + "' from " + event.carrier());
        }
        return new TrackingUpdate(event.carrier() + ":" + event.awb(), status); // (3)!
    }
}
