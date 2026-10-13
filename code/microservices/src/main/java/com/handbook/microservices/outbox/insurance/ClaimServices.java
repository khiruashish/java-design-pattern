package com.handbook.microservices.outbox.insurance;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

/** The same claim submission written twice: with a dual write, and with an outbox. */
public final class ClaimServices {

    public static final String[] SCHEMA = {
            "CREATE TABLE claims (id VARCHAR PRIMARY KEY, policy VARCHAR, amount BIGINT)",
            "CREATE TABLE outbox (id BIGINT AUTO_INCREMENT PRIMARY KEY, aggregate_id VARCHAR, "
                    + "type VARCHAR, payload VARCHAR, published BOOLEAN DEFAULT FALSE)"
    };

    private ClaimServices() {
    }

    /** ❌ Commit to the database, then publish. A failure in between loses the event. */
    public static void submitWithDualWrite(DataSource db, Consumer<String> broker, String claimId,
                                           String policy, long amount) throws SQLException {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO claims VALUES (?, ?, ?)")) {
            ps.setString(1, claimId);
            ps.setString(2, policy);
            ps.setLong(3, amount);
            ps.executeUpdate(); // auto-commit: the claim is saved now
        }
        broker.accept("ClaimSubmitted " + claimId); // (1)!
    }

    /** ✅ The claim and the event row commit together; a relay publishes later. */
    public static void submitWithOutbox(DataSource db, String claimId, String policy, long amount)
            throws SQLException {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement claim = c.prepareStatement("INSERT INTO claims VALUES (?, ?, ?)");
                 PreparedStatement event = c.prepareStatement(
                         "INSERT INTO outbox (aggregate_id, type, payload) VALUES (?, 'ClaimSubmitted', ?)")) {
                claim.setString(1, claimId);
                claim.setString(2, policy);
                claim.setLong(3, amount);
                claim.executeUpdate();
                event.setString(1, claimId);
                event.setString(2, "ClaimSubmitted " + claimId);
                event.executeUpdate();
                c.commit(); // (2)!
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
