package com.handbook.structural.adapter.logistics;

import java.util.Map;

public class QuickShipAdapter implements TrackingProvider {

    private final QuickShipApi api;

    public QuickShipAdapter(QuickShipApi api) {
        this.api = api;
    }

    @Override
    public Tracking track(String trackingId) {
        Map<String, Object> raw = api.fetch(Long.parseLong(trackingId.replace("QS-", ""))); // (1)!
        Status status = switch ((int) raw.get("stage")) {
            case 1 -> Status.PICKED_UP;
            case 2 -> Status.IN_TRANSIT;
            case 3 -> Status.OUT_FOR_DELIVERY;
            case 4 -> Status.DELIVERED;
            default -> throw new IllegalStateException("Unknown QuickShip stage " + raw.get("stage"));
        };
        return new Tracking("QuickShip", trackingId, status, (String) raw.get("hub"));
    }
}
