package com.handbook.behavioral.specification.courier;

import com.handbook.behavioral.specification.core.Spec;
import com.handbook.behavioral.specification.courier.CourierSelection.Courier;
import com.handbook.behavioral.specification.courier.CourierSelection.Shipment;

import java.util.List;
import java.util.Set;

public class CourierDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: Which Couriers Can Take This Parcel? ===");
        List<Courier> couriers = List.of(
                new Courier("SwiftShip", 10, false, true, Set.of("411001", "560034")),
                new Courier("HeavyHaul", 50, true, false, Set.of("411001", "110001")),
                new Courier("CityDash", 5, false, true, Set.of("560034")));

        List<Shipment> shipments = List.of(
                new Shipment(2.5, false, true, "560034"),
                new Shipment(18, true, false, "411001"),
                new Shipment(7, false, true, "110001"));

        for (Shipment s : shipments) {
            Spec<Courier> spec = CourierSelection.canCarry(s);
            List<String> ok = couriers.stream().filter(spec::isSatisfiedBy).map(Courier::name).toList();
            System.out.println(spec.describe());
            System.out.println("  -> " + (ok.isEmpty() ? "no courier available, flag for manual booking" : ok));
        }
    }
}
