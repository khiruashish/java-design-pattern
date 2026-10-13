package com.handbook.microservices.dbperservice.replication;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Keeps a local, read-only copy of the customer data it needs (delivery address), updated from
 * CustomerAddressChanged events. Creating a shipping label needs no call to customer-service.
 */
public final class ShippingService {

    public record CustomerAddressChanged(String customerId, String address, long version) {
    }

    private final DataSource db = Db.inMemory("shipping_db",
            "CREATE TABLE customer_address_replica (customer_id VARCHAR PRIMARY KEY, address VARCHAR, version BIGINT)"); // (1)!

    public void on(CustomerAddressChanged e) {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "MERGE INTO customer_address_replica r USING (VALUES (?, ?, ?)) v(id, addr, ver) "
                             + "ON r.customer_id = v.id "
                             + "WHEN MATCHED AND r.version < v.ver THEN UPDATE SET address = v.addr, version = v.ver " // (2)!
                             + "WHEN NOT MATCHED THEN INSERT VALUES (v.id, v.addr, v.ver)")) {
            ps.setString(1, e.customerId());
            ps.setString(2, e.address());
            ps.setLong(3, e.version());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public String createLabel(String orderId, String customerId) {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT address FROM customer_address_replica WHERE customer_id = ?")) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? "LABEL " + orderId + " -> " + rs.getString(1) : "LABEL " + orderId + " -> address unknown";
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
