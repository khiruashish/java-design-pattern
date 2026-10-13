package com.handbook.microservices.dbperservice.grocery;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Owns the customers database. Other services can only reach this data through its API. */
public final class CustomerService {

    private final DataSource db = Db.inMemory("customers_db",
            "CREATE TABLE customers (id VARCHAR PRIMARY KEY, name VARCHAR, phone VARCHAR)",
            "INSERT INTO customers VALUES ('CUST-42', 'Asha Rao', '+91-98450-11111'),"
                    + " ('CUST-77', 'Vikram Singh', '+91-98450-22222')");
    private final AtomicInteger apiCalls = new AtomicInteger();

    /** GET /customers/{id}/name */
    public String name(String id) {
        apiCalls.incrementAndGet();
        return names(List.of(id)).getOrDefault(id, "?");
    }

    /** GET /customers?ids=a,b,c (batch endpoint) */
    public Map<String, String> namesBatch(Collection<String> ids) {
        apiCalls.incrementAndGet();
        return names(ids);
    }

    private Map<String, String> names(Collection<String> ids) {
        Map<String, String> result = new LinkedHashMap<>();
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name FROM customers WHERE id IN (" + placeholders + ")")) {
            int i = 1;
            for (String id : ids) {
                ps.setString(i++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    public int apiCalls() {
        return apiCalls.get();
    }
}
