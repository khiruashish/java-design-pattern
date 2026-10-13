package com.handbook.microservices.outbox.insurance;

import com.handbook.microservices.outbox.grocery.OutboxRelay;
import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DualWriteDemo {

    public static void main(String[] args) throws SQLException {
        List<String> fraudCheckInbox = new ArrayList<>();
        Consumer<String> flakyBroker = event -> {
            if (event.endsWith("CLM-2")) {
                throw new IllegalStateException("broker timeout"); // a blip at exactly the wrong moment
            }
            fraudCheckInbox.add(event);
        };

        System.out.println("=== Insurance: claim submitted -> fraud check must always run ===");
        System.out.println("-- dual write (save, then publish) --");
        DataSource naive = Db.inMemory("claims_naive", ClaimServices.SCHEMA);
        for (String id : List.of("CLM-1", "CLM-2", "CLM-3")) {
            try {
                ClaimServices.submitWithDualWrite(naive, flakyBroker, id, "POL-9", 250_000);
            } catch (IllegalStateException e) {
                System.out.println("  " + id + ": saved, but publish failed: " + e.getMessage());
            }
        }
        System.out.println("claims saved: " + Db.count(naive, "SELECT COUNT(*) FROM claims")
                + ", fraud checks received: " + fraudCheckInbox);
        System.out.println("CLM-2 will be paid without a fraud check.");

        System.out.println("-- transactional outbox --");
        fraudCheckInbox.clear();
        DataSource db = Db.inMemory("claims_outbox", ClaimServices.SCHEMA);
        for (String id : List.of("CLM-1", "CLM-2", "CLM-3")) {
            ClaimServices.submitWithOutbox(db, id, "POL-9", 250_000);
        }
        OutboxRelay relay = new OutboxRelay(db, (key, payload) -> flakyBroker.accept(payload));
        relay.poll(10); // CLM-1 published; CLM-2 fails, so the relay stops and keeps order
        new OutboxRelay(db, (key, payload) -> fraudCheckInbox.add(payload)).poll(10); // next poll, broker healthy
        System.out.println("claims saved: " + Db.count(db, "SELECT COUNT(*) FROM claims")
                + ", fraud checks received: " + fraudCheckInbox);
    }
}
