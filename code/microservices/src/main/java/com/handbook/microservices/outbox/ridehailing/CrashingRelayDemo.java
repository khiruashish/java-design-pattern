package com.handbook.microservices.outbox.ridehailing;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CrashingRelayDemo {

    public static void main(String[] args) throws SQLException {
        DataSource db = Db.inMemory("trips",
                "CREATE TABLE outbox (id BIGINT AUTO_INCREMENT PRIMARY KEY, payload VARCHAR, published BOOLEAN DEFAULT FALSE)",
                "INSERT INTO outbox (payload) VALUES ('TripCompleted TRIP-1 ₹212'), ('TripCompleted TRIP-2 ₹96')");
        var billing = new TripEventsConsumer();

        System.out.println("=== Ride-hailing: relay crashes between publish and mark ===");
        relay(db, billing, true);
        System.out.println("-- relay restarts --");
        relay(db, billing, false);
        System.out.println("Events delivered: 3, receipts sent: " + billing.fareReceiptsSent() + " (exactly once per trip)");
    }

    /** A relay that publishes with the row ID as the event ID; optionally crashes before marking. */
    private static void relay(DataSource db, TripEventsConsumer consumer, boolean crashAfterFirstPublish)
            throws SQLException {
        try (Connection c = db.getConnection(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, payload FROM outbox WHERE published = FALSE ORDER BY id");
             PreparedStatement mark = c.prepareStatement("UPDATE outbox SET published = TRUE WHERE id = ?")) {
            while (rs.next()) {
                consumer.onEvent(rs.getLong("id"), rs.getString("payload"));
                if (crashAfterFirstPublish) {
                    System.out.println("  relay: CRASH before marking event " + rs.getLong("id"));
                    return;
                }
                mark.setLong(1, rs.getLong("id"));
                mark.executeUpdate();
            }
        }
    }
}
