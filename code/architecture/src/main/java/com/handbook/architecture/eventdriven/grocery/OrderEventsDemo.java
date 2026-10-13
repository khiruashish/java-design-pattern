package com.handbook.architecture.eventdriven.grocery;

import com.handbook.architecture.eventdriven.broker.MiniBroker;

public class OrderEventsDemo {

    record OrderPlaced(String orderId, String customerId, long totalPaise) {
    }

    public static void main(String[] args) {
        MiniBroker broker = new MiniBroker();

        // Each consumer group owns its own reaction. The producer knows none of them.
        broker.<OrderPlaced>subscribe("orders", "inventory", e -> System.out.println("    inventory     : reserve stock for " + e.orderId()));
        broker.<OrderPlaced>subscribe("orders", "loyalty", e -> System.out.println("    loyalty       : +" + e.totalPaise() / 10_000 + " points to " + e.customerId()));
        broker.<OrderPlaced>subscribe("orders", "email", e -> System.out.println("    email-1       : receipt for " + e.orderId()));
        broker.<OrderPlaced>subscribe("orders", "email", e -> System.out.println("    email-2       : receipt for " + e.orderId()));

        System.out.println("=== Grocery: OrderPlaced fan-out to independent consumers ===");
        broker.publish("orders", new OrderPlaced("ORD-1", "CUST-42", 144_700));
        broker.publish("orders", new OrderPlaced("ORD-2", "CUST-77", 64_900));

        System.out.println("-- analytics team adds a consumer; the order service is not touched --");
        broker.<OrderPlaced>subscribe("orders", "analytics", e -> System.out.println("    analytics     : basket value " + e.totalPaise() / 100));
        broker.publish("orders", new OrderPlaced("ORD-3", "CUST-42", 12_900));
    }
}
