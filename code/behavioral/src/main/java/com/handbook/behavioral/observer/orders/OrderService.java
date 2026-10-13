package com.handbook.behavioral.observer.orders;

import java.util.List;

/** The subject's owner: places the order, then announces it. Knows nothing about email or analytics. */
public class OrderService {

    private final OrderEvents events;
    private int next = 1;

    public OrderService(OrderEvents events) {
        this.events = events;
    }

    public String place(String email, List<String> skus, int amountInr) {
        String id = "ORD-" + next++;
        System.out.println("Order " + id + " saved");
        events.publish(new OrderEvents.OrderPlaced(id, email, skus, amountInr)); // (1)!
        return id;
    }
}
