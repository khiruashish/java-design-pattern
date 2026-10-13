package com.handbook.microservices.outbox.grocery;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrderOutboxDemo {

    public static void main(String[] args) throws SQLException {
        DataSource db = Db.inMemory("orders", OrderService.SCHEMA);
        var orders = new OrderService(db);
        AtomicBoolean brokerUp = new AtomicBoolean(false);
        var relay = new OutboxRelay(db, (key, payload) -> {
            if (!brokerUp.get()) {
                throw new IllegalStateException("connection refused");
            }
            System.out.println("  kafka: orders.events key=" + key + " " + payload);
        });

        System.out.println("=== Grocery: order + OrderPlaced event in one transaction ===");
        orders.placeOrder("ORD-1", "CUST-42", 79_800);
        orders.placeOrder("ORD-2", "CUST-77", 64_900);
        try {
            orders.placeOrder("ORD-1", "CUST-42", 79_800); // duplicate submit: primary key violation
        } catch (SQLException e) {
            System.out.println("Duplicate ORD-1 rejected; transaction rolled back (no order, no event)");
        }
        status(db);

        System.out.println("-- relay polls while Kafka is down --");
        System.out.println("published " + relay.poll(10));
        status(db);

        System.out.println("-- Kafka is back --");
        brokerUp.set(true);
        System.out.println("published " + relay.poll(10));
        status(db);
    }

    private static void status(DataSource db) {
        System.out.println("orders=" + Db.count(db, "SELECT COUNT(*) FROM orders")
                + " outbox=" + Db.count(db, "SELECT COUNT(*) FROM outbox")
                + " unpublished=" + Db.count(db, "SELECT COUNT(*) FROM outbox WHERE published = FALSE"));
    }
}
