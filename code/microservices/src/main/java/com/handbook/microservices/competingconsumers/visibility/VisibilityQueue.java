package com.handbook.microservices.competingconsumers.visibility;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** SQS-style queue: a received message is hidden, not deleted, until the worker acks it. */
public final class VisibilityQueue {

    public record Message(String id, String body, int receiveCount) { }

    private record InFlight(Message message, Instant visibleAgainAt) { }

    private final Deque<Message> visible = new ArrayDeque<>();
    private final Map<String, InFlight> inFlight = new HashMap<>();
    private final Clock clock;
    private final Duration visibilityTimeout;

    public VisibilityQueue(Clock clock, Duration visibilityTimeout) {
        this.clock = clock;
        this.visibilityTimeout = visibilityTimeout;
    }

    public void send(String id, String body) {
        visible.add(new Message(id, body, 0));
    }

    public Optional<Message> receive() {
        returnExpired();
        Message m = visible.poll();
        if (m == null) {
            return Optional.empty();
        }
        Message delivered = new Message(m.id(), m.body(), m.receiveCount() + 1);
        inFlight.put(m.id(), new InFlight(delivered, clock.instant().plus(visibilityTimeout))); // (1)!
        return Optional.of(delivered);
    }

    public void ack(String id) {
        inFlight.remove(id); // (2)!
    }

    private void returnExpired() {
        inFlight.values().removeIf(f -> {
            if (!clock.instant().isBefore(f.visibleAgainAt())) {
                visible.addFirst(f.message()); // (3)!
                return true;
            }
            return false;
        });
    }
}
