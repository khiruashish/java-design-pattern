package com.handbook.structural.adapter.logistics;

import java.util.Map;

public class TrackingDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: One Tracking Page, Many Couriers ===");
        Map<String, TrackingProvider> byCarrier = Map.of(
                "GX", new GlobalExpressAdapter(new GlobalExpressClient()),
                "QS", new QuickShipAdapter(new QuickShipApi()));

        for (String id : new String[] {"GX-7781230", "QS-554201"}) {
            TrackingProvider provider = byCarrier.get(id.substring(0, 2));
            System.out.println(provider.track(id));
        }
    }
}
