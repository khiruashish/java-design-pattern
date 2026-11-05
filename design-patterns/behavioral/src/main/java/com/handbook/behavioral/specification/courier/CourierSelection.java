package com.handbook.behavioral.specification.courier;

import com.handbook.behavioral.specification.core.Spec;

import java.util.Set;

/** Specifications built from the SHIPMENT, applied to each courier. */
public final class CourierSelection {

    private CourierSelection() {
    }

    public record Courier(String name, int maxKg, boolean hazmat, boolean cod, Set<String> pincodes) {
    }

    public record Shipment(double kg, boolean hazardous, boolean cashOnDelivery, String pincode) {
    }

    public static Spec<Courier> canCarry(Shipment s) { // (1)!
        Spec<Courier> spec = Spec.of("max weight >= " + s.kg() + " kg", c -> c.maxKg() >= s.kg());
        spec = spec.and(Spec.of("serves " + s.pincode(), c -> c.pincodes().contains(s.pincode())));
        if (s.hazardous()) {
            spec = spec.and(Spec.of("hazmat certified", Courier::hazmat));
        }
        if (s.cashOnDelivery()) {
            spec = spec.and(Spec.of("collects COD", Courier::cod));
        }
        return spec;
    }
}
