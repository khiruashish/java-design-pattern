package com.handbook.behavioral.observer.orders;

import java.util.List;

public class OrdersDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Reacting to OrderPlaced ===");
        OrderEvents events = new OrderEvents();

        events.onOrderPlaced(e -> System.out.println("  [email] confirmation sent to " + e.email()));
        events.onOrderPlaced(e -> System.out.println("  [inventory] reserved " + e.skus()));
        events.onOrderPlaced(e -> {
            if (e.amountInr() > 50_000) {
                throw new IllegalStateException("analytics endpoint timed out");
            }
            System.out.println("  [analytics] revenue +" + e.amountInr());
        });
        events.onOrderPlaced(e -> System.out.println("  [loyalty] +" + e.amountInr() / 100 + " points"));

        OrderService orders = new OrderService(events);
        orders.place("asha@example.com", List.of("PHONE-CASE"), 499);
        orders.place("kabir@example.com", List.of("LAPTOP-14"), 72_000);
    }
}
