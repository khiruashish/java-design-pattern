package com.handbook.architecture.hexagonal.checkout.adapter.out;

import com.handbook.architecture.hexagonal.checkout.domain.Order;
import com.handbook.architecture.hexagonal.checkout.port.out.OrderRepositoryPort;

import java.util.ArrayList;
import java.util.List;

/** Driven adapter for persistence. A JPA or JDBC adapter would implement the same port. */
public class InMemoryOrderRepository implements OrderRepositoryPort {

    private final List<Order> orders = new ArrayList<>();

    @Override
    public void save(Order order) {
        orders.add(order);
    }

    @Override
    public int count() {
        return orders.size();
    }
}
