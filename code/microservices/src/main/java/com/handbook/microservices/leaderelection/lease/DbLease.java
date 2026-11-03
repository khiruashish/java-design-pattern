package com.handbook.microservices.leaderelection.lease;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;

/** Leader election with a lease row: whoever holds an unexpired lease is the leader. */
public final class DbLease {

    private final DataSource ds = Db.inMemory("leases",
            "CREATE TABLE lease (name VARCHAR PRIMARY KEY, holder VARCHAR, expires_at TIMESTAMP)",
            "INSERT INTO lease VALUES ('substitution-report', NULL, TIMESTAMP '1970-01-01 00:00:00')");
    private final Clock clock;
    private final Duration ttl;

    public DbLease(Clock clock, Duration ttl) {
        this.clock = clock;
        this.ttl = ttl;
    }

    /** Acquire if free/expired, or renew if we already hold it. One atomic UPDATE: no race. */
    public boolean tryAcquireOrRenew(String pod) {
        String sql = "UPDATE lease SET holder = ?, expires_at = ? "
                + "WHERE name = 'substitution-report' AND (holder = ? OR expires_at < ?)"; // (1)!
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            Timestamp now = Timestamp.from(clock.instant());
            ps.setString(1, pod);
            ps.setTimestamp(2, Timestamp.from(clock.instant().plus(ttl)));
            ps.setString(3, pod);
            ps.setTimestamp(4, now);
            return ps.executeUpdate() == 1; // (2)!
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
