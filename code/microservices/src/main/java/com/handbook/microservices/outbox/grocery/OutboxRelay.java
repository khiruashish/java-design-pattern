package com.handbook.microservices.outbox.grocery;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;

/**
 * Polls unpublished outbox rows in insertion order, publishes each to the broker, then marks it
 * published. If publishing fails, the row stays and is retried on the next poll (at-least-once).
 */
public final class OutboxRelay {

    private final DataSource db;
    private final BiConsumer<String, String> broker; // (topic key, payload)

    public OutboxRelay(DataSource db, BiConsumer<String, String> broker) {
        this.db = db;
        this.broker = broker;
    }

    /** Returns how many events were published in this poll. */
    public int poll(int batchSize) throws SQLException {
        int published = 0;
        try (Connection c = db.getConnection();
             PreparedStatement select = c.prepareStatement(
                     "SELECT id, aggregate_id, payload FROM outbox WHERE published = FALSE ORDER BY id LIMIT ?");
             PreparedStatement mark = c.prepareStatement("UPDATE outbox SET published = TRUE WHERE id = ?")) {
            select.setInt(1, batchSize);
            try (ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    try {
                        broker.accept(rs.getString("aggregate_id"), rs.getString("payload")); // (1)!
                    } catch (RuntimeException brokerDown) {
                        System.out.println("  relay: broker unavailable (" + brokerDown.getMessage() + "), will retry");
                        return published; // (2)!
                    }
                    mark.setLong(1, rs.getLong("id"));
                    mark.executeUpdate();
                    published++;
                }
            }
        }
        return published;
    }
}
