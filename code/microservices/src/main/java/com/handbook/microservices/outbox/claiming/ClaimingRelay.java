package com.handbook.microservices.outbox.claiming;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;

/**
 * Coding challenge: several relay instances (for availability) must not publish the same row.
 * Each relay atomically claims a batch with a conditional UPDATE, then publishes only its claim.
 * (On PostgreSQL you would use SELECT ... FOR UPDATE SKIP LOCKED instead.)
 */
public final class ClaimingRelay {

    private final DataSource db;
    private final String relayId;

    public ClaimingRelay(DataSource db, String relayId) {
        this.db = db;
        this.relayId = relayId;
    }

    public int pollOnce(int batchSize, LongConsumer publish) throws SQLException {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            List<Long> claimed = new ArrayList<>();
            try (PreparedStatement select = c.prepareStatement(
                    "SELECT id FROM outbox WHERE claimed_by IS NULL ORDER BY id LIMIT ?");
                 PreparedStatement claim = c.prepareStatement(
                         "UPDATE outbox SET claimed_by = ? WHERE id = ? AND claimed_by IS NULL")) {
                select.setInt(1, batchSize);
                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        claim.setString(1, relayId);
                        claim.setLong(2, rs.getLong(1));
                        if (claim.executeUpdate() == 1) { // 0 rows means another relay won this one
                            claimed.add(rs.getLong(1));
                        }
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
            claimed.forEach(publish::accept);
            return claimed.size();
        }
    }
}
