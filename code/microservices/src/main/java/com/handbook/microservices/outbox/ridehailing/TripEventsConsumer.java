package com.handbook.microservices.outbox.ridehailing;

import java.util.HashSet;
import java.util.Set;

/**
 * Outbox relays are at-least-once: if the relay crashes after publishing but before marking the
 * row, the event is published again. Consumers deduplicate by event ID.
 */
public final class TripEventsConsumer {

    private final Set<Long> processedEventIds = new HashSet<>(); // a processed_events table in real life
    private int fareReceiptsSent;

    public void onEvent(long eventId, String payload) {
        if (!processedEventIds.add(eventId)) { // (1)!
            System.out.println("  billing: duplicate event " + eventId + " ignored");
            return;
        }
        fareReceiptsSent++;
        System.out.println("  billing: receipt sent for " + payload);
    }

    public int fareReceiptsSent() {
        return fareReceiptsSent;
    }
}
