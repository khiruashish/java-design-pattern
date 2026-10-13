package com.handbook.microservices.eventsourcing.shipment;

import java.time.Instant;
import java.util.List;

/** Shipment events with timestamps support "what did we know at time T?" queries. */
public final class ShipmentHistory {

    public record Scanned(Instant at, String location, String status) {
    }

    public record State(String location, String status) {
    }

    private final List<Scanned> events;

    public ShipmentHistory(List<Scanned> events) {
        this.events = List.copyOf(events);
    }

    public State current() {
        return stateAt(Instant.MAX);
    }

    public State stateAt(Instant when) { // (1)!
        State state = new State("-", "CREATED");
        for (Scanned e : events) {
            if (e.at().isAfter(when)) {
                break;
            }
            state = new State(e.location(), e.status());
        }
        return state;
    }
}
