package com.handbook.architecture.layered.grocery.repository;

import com.handbook.architecture.layered.grocery.domain.Order;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Persistence layer: stores and loads domain objects. A JPA/JdbcTemplate repository in a real app. */
public class OrderRepository {

    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, Long> prices = Map.of("MANGO-1KG", 39_900L, "RICE-5KG", 64_900L);

    public Optional<Long> priceOf(String sku) {
        return Optional.ofNullable(prices.get(sku));
    }

    public void save(Order order) {
        System.out.println("    [repository] INSERT order " + order.id());
        orders.put(order.id(), order);
    }

    public int count() {
        return orders.size();
    }
}
