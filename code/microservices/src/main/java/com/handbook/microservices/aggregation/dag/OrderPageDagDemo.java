package com.handbook.microservices.aggregation.dag;

import com.handbook.microservices.aggregation.dag.DependencyAggregator.Step;

import java.util.List;
import java.util.Map;

public class OrderPageDagDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: Order-Details Page with Dependent Calls ===");
        List<Step> steps = List.of(
                new Step("order", 50, List.of()),
                new Step("customer", 40, List.of()),
                new Step("shipment", 80, List.of("order")),     // needs order.shipmentId
                new Step("products", 60, List.of("order")),     // needs order.skus
                new Step("driver", 30, List.of("shipment")),    // needs shipment.driverId
                new Step("refundRules", 20, List.of("products", "customer")));
        Map<String, Long> finish = DependencyAggregator.schedule(steps);
        finish.forEach((name, t) -> System.out.println("  " + name + " done at " + t + " ms"));
        System.out.println("Parallel where possible: " + finish.values().stream().mapToLong(Long::longValue).max().orElseThrow()
                + " ms; one call after another: " + DependencyAggregator.sequential(steps) + " ms");
    }
}
