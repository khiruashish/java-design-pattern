package com.handbook.enterprise.domainevents.timing;

import java.util.ArrayList;
import java.util.List;

/** Coding challenge: an SMS for an order that never existed. Publish AFTER commit. */
public class PublishTimingDemo {

    static final List<String> SMS_SENT = new ArrayList<>();

    static void sendSms(String text) { SMS_SENT.add(text); }

    /** Simulated transaction: runs the body, then the commit can fail (e.g., unique key, deadlock). */
    static void inTransaction(boolean commitFails, Runnable body, Runnable afterCommit) {
        body.run();
        if (commitFails) {
            System.out.println("  COMMIT failed -> rolled back");
            return;
        }
        System.out.println("  COMMIT ok");
        afterCommit.run();
    }

    /** Events recorded during the transaction live and die with it. */
    static void placeOrder(String orderId, boolean commitFails) {
        List<String> pending = new ArrayList<>();
        inTransaction(commitFails, () -> pending.add(orderId + " confirmed"), () -> pending.forEach(PublishTimingDemo::sendSms));
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: When Should Events Be Published? ===");
        System.out.println("Publishing immediately inside the use case:");
        inTransaction(true, () -> sendSms("ORD-77 confirmed"), () -> { });
        System.out.println("  SMS sent: " + SMS_SENT + "  <- customer told about an order that doesn't exist");

        SMS_SENT.clear();
        System.out.println("Recording, then publishing after commit:");
        placeOrder("ORD-77", true);
        System.out.println("  SMS sent: " + SMS_SENT);
        placeOrder("ORD-78", false);
        System.out.println("  SMS sent: " + SMS_SENT);
        System.out.println("In Spring: @TransactionalEventListener(phase = AFTER_COMMIT), or an outbox row for other services.");
    }
}
