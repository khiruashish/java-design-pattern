package com.handbook.microservices.idempotency.notifications;

import com.handbook.microservices.support.FakeClock;

import java.time.Duration;

public class SmsDedupeDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T07:00:00Z");
        var dedupe = new SmsDeduplicator(Duration.ofHours(24), clock);

        System.out.println("=== Food delivery: \"Your order is out for delivery\" SMS ===");
        send(dedupe, "ORD-9:OUT_FOR_DELIVERY");
        clock.advance(Duration.ofSeconds(4));
        send(dedupe, "ORD-9:OUT_FOR_DELIVERY"); // the courier app sent the status twice
        send(dedupe, "ORD-9:DELIVERED");
        send(dedupe, "ORD-10:OUT_FOR_DELIVERY");
        System.out.println("keys remembered: " + dedupe.size());
        clock.advance(Duration.ofHours(25));
        send(dedupe, "ORD-11:OUT_FOR_DELIVERY");
        System.out.println("keys remembered after 25 h: " + dedupe.size() + " (old keys expired)");
    }

    private static void send(SmsDeduplicator dedupe, String key) {
        System.out.println(key + " -> " + (dedupe.firstTime(key) ? "SMS sent" : "duplicate, not sent"));
    }
}
