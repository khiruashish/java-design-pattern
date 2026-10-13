package com.handbook.architecture.layered.grocery.service;

import com.handbook.architecture.layered.grocery.domain.Order;
import com.handbook.architecture.layered.grocery.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Business layer: use cases, validation and transaction boundaries. Calls only the layer below. */
public class OrderService {

    public static final class InvalidOrderException extends RuntimeException {
        InvalidOrderException(String message) {
            super(message);
        }
    }

    private final OrderRepository repository;
    private int nextId = 1;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    /** In Spring this method would be @Transactional: the use case is one unit of work. */
    public Order placeOrder(String customerId, Map<String, Integer> quantities) { // (1)!
        System.out.println("    [service] validating and pricing " + quantities.keySet());
        if (quantities.values().stream().mapToInt(Integer::intValue).sum() > 20) {
            throw new InvalidOrderException("max 20 items per order");
        }
        List<Order.Line> lines = new ArrayList<>();
        quantities.forEach((sku, qty) -> lines.add(new Order.Line(sku, qty,
                repository.priceOf(sku).orElseThrow(() -> new InvalidOrderException("unknown product " + sku)))));
        Order order = new Order("ORD-" + nextId++, customerId, List.copyOf(lines));
        repository.save(order);
        return order;
    }
}
