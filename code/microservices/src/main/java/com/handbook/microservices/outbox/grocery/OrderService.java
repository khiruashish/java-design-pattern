package com.handbook.microservices.outbox.grocery;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Writes the order AND the event describing it in ONE local transaction.
 * Either both are committed or neither is, so no event is ever lost or invented.
 */
public final class OrderService {

    public static final String[] SCHEMA = {
            "CREATE TABLE orders (id VARCHAR PRIMARY KEY, customer VARCHAR, total_paise BIGINT)",
            "CREATE TABLE outbox (id BIGINT AUTO_INCREMENT PRIMARY KEY, aggregate_id VARCHAR, "
                    + "type VARCHAR, payload VARCHAR, published BOOLEAN DEFAULT FALSE)"
    };

    private final DataSource db;

    public OrderService(DataSource db) {
        this.db = db;
    }

    public void placeOrder(String orderId, String customer, long totalPaise) throws SQLException {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false); // (1)!
            try (PreparedStatement order = c.prepareStatement("INSERT INTO orders VALUES (?, ?, ?)");
                 PreparedStatement event = c.prepareStatement(
                         "INSERT INTO outbox (aggregate_id, type, payload) VALUES (?, ?, ?)")) {
                order.setString(1, orderId);
                order.setString(2, customer);
                order.setLong(3, totalPaise);
                order.executeUpdate();

                event.setString(1, orderId);
                event.setString(2, "OrderPlaced");
                event.setString(3, "{\"orderId\":\"" + orderId + "\",\"total\":" + totalPaise + "}");
                event.executeUpdate(); // (2)!

                c.commit();
            } catch (SQLException e) {
                c.rollback(); // (3)!
                throw e;
            }
        }
    }
}
