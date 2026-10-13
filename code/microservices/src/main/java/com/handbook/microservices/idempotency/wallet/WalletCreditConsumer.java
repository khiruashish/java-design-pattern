package com.handbook.microservices.idempotency.wallet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Credits a wallet for each RefundIssued message. The message ID is inserted into
 * processed_messages in the SAME transaction as the credit; a duplicate violates the primary key,
 * the whole transaction rolls back, and the duplicate is acknowledged without effect.
 */
public final class WalletCreditConsumer {

    public static final String[] SCHEMA = {
            "CREATE TABLE wallets (customer VARCHAR PRIMARY KEY, balance BIGINT)",
            "CREATE TABLE processed_messages (message_id VARCHAR PRIMARY KEY, processed_at TIMESTAMP)" // (1)!
    };

    private final DataSource db;

    public WalletCreditConsumer(DataSource db) {
        this.db = db;
    }

    /** Returns true if the message had an effect, false if it was a duplicate. */
    public boolean onRefundIssued(String messageId, String customer, long paise) throws SQLException {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement dedupe = c.prepareStatement(
                    "INSERT INTO processed_messages VALUES (?, CURRENT_TIMESTAMP)");
                 PreparedStatement credit = c.prepareStatement(
                         "MERGE INTO wallets w USING (VALUES (?, ?)) v(c, amt) ON w.customer = v.c "
                                 + "WHEN MATCHED THEN UPDATE SET balance = balance + v.amt "
                                 + "WHEN NOT MATCHED THEN INSERT VALUES (v.c, v.amt)")) {
                dedupe.setString(1, messageId);
                dedupe.executeUpdate(); // (2)!
                credit.setString(1, customer);
                credit.setLong(2, paise);
                credit.executeUpdate();
                c.commit(); // (3)!
                return true;
            } catch (SQLException e) {
                c.rollback();
                if ("23505".equals(e.getSQLState())) { // unique violation: already processed
                    return false;
                }
                throw e;
            }
        }
    }
}
