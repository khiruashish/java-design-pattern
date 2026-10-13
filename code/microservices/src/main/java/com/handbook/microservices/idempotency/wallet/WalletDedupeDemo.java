package com.handbook.microservices.idempotency.wallet;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.SQLException;

public class WalletDedupeDemo {

    public static void main(String[] args) throws SQLException {
        DataSource db = Db.inMemory("wallet", WalletCreditConsumer.SCHEMA);
        var consumer = new WalletCreditConsumer(db);

        System.out.println("=== Grocery: refund messages credited to the wallet exactly once ===");
        String[][] deliveries = {
                {"msg-501", "CUST-42", "19900"},
                {"msg-502", "CUST-42", "4500"},
                {"msg-501", "CUST-42", "19900"}, // redelivered after a consumer rebalance
                {"msg-502", "CUST-42", "4500"},  // redelivered again
        };
        for (String[] d : deliveries) {
            boolean applied = consumer.onRefundIssued(d[0], d[1], Long.parseLong(d[2]));
            System.out.println(d[0] + " ₹" + Long.parseLong(d[2]) / 100 + " -> " + (applied ? "credited" : "duplicate, skipped"));
        }
        System.out.println("Wallet balance: ₹" + Db.count(db, "SELECT balance FROM wallets WHERE customer = 'CUST-42'") / 100
                + " (expected ₹244)");
    }
}
