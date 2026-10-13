package com.handbook.microservices.dbperservice.grocery;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Owns the orders database. It stores customer IDs, never customer details. */
public final class OrderService {

    public record OrderRow(String id, String customerId, long totalPaise) {
    }

    private final DataSource db = Db.inMemory("orders_db",
            "CREATE TABLE orders (id VARCHAR PRIMARY KEY, customer_id VARCHAR, total_paise BIGINT)", // (1)!
            "INSERT INTO orders VALUES ('ORD-1', 'CUST-42', 79800), ('ORD-2', 'CUST-77', 64900),"
                    + " ('ORD-3', 'CUST-42', 12900), ('ORD-4', 'CUST-77', 39900)");

    public List<OrderRow> recentOrders() {
        return query("SELECT id, customer_id, total_paise FROM orders ORDER BY id");
    }

    /** What a monolith developer would try first. */
    public List<OrderRow> recentOrdersWithJoin() {
        return query("SELECT o.id, c.name AS customer_id, o.total_paise FROM orders o "
                + "JOIN customers c ON c.id = o.customer_id"); // (2)!
    }

    private List<OrderRow> query(String sql) {
        List<OrderRow> rows = new ArrayList<>();
        try (Connection c = db.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new OrderRow(rs.getString(1), rs.getString(2), rs.getLong(3)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage().split(";")[0], e);
        }
        return rows;
    }
}
