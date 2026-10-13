package com.handbook.microservices.dbperservice.grocery;

import java.util.List;

/** API composition: query the owning services and join the results in memory. */
public final class OrderHistoryComposer {

    private final OrderService orders;
    private final CustomerService customers;

    public OrderHistoryComposer(OrderService orders, CustomerService customers) {
        this.orders = orders;
        this.customers = customers;
    }

    public List<String> history() {
        return orders.recentOrders().stream()
                .map(o -> o.id() + " " + customers.name(o.customerId()) + " ₹" + o.totalPaise() / 100) // (1)!
                .toList();
    }
}
