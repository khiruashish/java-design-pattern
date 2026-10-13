package com.handbook.microservices.dbperservice.batching;

import com.handbook.microservices.dbperservice.grocery.CustomerService;
import com.handbook.microservices.dbperservice.grocery.OrderService;
import com.handbook.microservices.dbperservice.grocery.OrderService.OrderRow;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Coding challenge: the same composition with ONE batch call instead of one call per order (N+1). */
public final class BatchedComposer {

    private final OrderService orders;
    private final CustomerService customers;

    public BatchedComposer(OrderService orders, CustomerService customers) {
        this.orders = orders;
        this.customers = customers;
    }

    public List<String> history() {
        List<OrderRow> rows = orders.recentOrders();
        Set<String> ids = new TreeSet<>();
        rows.forEach(r -> ids.add(r.customerId())); // collect distinct IDs first
        Map<String, String> names = customers.namesBatch(ids); // one round trip
        return rows.stream()
                .map(o -> o.id() + " " + names.getOrDefault(o.customerId(), "(unknown customer)") + " ₹" + o.totalPaise() / 100)
                .toList();
    }
}
