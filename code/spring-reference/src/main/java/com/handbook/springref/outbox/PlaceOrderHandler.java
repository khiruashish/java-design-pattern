package com.handbook.springref.outbox;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The business write and the outbox insert share one @Transactional boundary. */
@Service
public class PlaceOrderHandler {

    private final JdbcTemplate jdbc;

    public PlaceOrderHandler(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public void placeOrder(String orderId, String customerId, long totalPaise) {
        jdbc.update("INSERT INTO orders (id, customer_id, total_paise) VALUES (?, ?, ?)",
                orderId, customerId, totalPaise);
        jdbc.update("INSERT INTO outbox (topic, aggregate_id, payload) VALUES (?, ?, ?)",
                "orders.placed", orderId,
                "{\"orderId\":\"" + orderId + "\",\"totalPaise\":" + totalPaise + "}");
    }
}
