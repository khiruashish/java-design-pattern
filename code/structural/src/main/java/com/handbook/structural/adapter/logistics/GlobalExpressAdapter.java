package com.handbook.structural.adapter.logistics;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalExpressAdapter implements TrackingProvider {

    private static final Pattern EVENT = Pattern.compile("code='(\\w+)' city='([^']+)'");
    private static final Map<String, Status> CODES = Map.of(
            "PU", Status.PICKED_UP, "IT", Status.IN_TRANSIT,
            "OFD", Status.OUT_FOR_DELIVERY, "DL", Status.DELIVERED);

    private final GlobalExpressClient client;

    public GlobalExpressAdapter(GlobalExpressClient client) {
        this.client = client;
    }

    @Override
    public Tracking track(String trackingId) {
        Matcher m = EVENT.matcher(client.getShipmentXml(trackingId));
        if (!m.find()) {
            throw new IllegalStateException("Unexpected GlobalExpress response for " + trackingId);
        }
        return new Tracking("GlobalExpress", trackingId, CODES.get(m.group(1)), m.group(2));
    }
}
