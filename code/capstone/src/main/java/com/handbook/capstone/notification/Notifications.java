package com.handbook.capstone.notification;

import com.handbook.capstone.shared.DomainEvents;
import com.handbook.capstone.shared.DomainEvents.OrderPlaced;
import com.handbook.capstone.shared.DomainEvents.OrderStatusChanged;
import com.handbook.capstone.shared.DomainEvents.StockLow;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/** Notification module: [Observer] of domain events, [Template Method] for messages, deduplicated. */
public final class Notifications {

    /** Template Method: the skeleton (prefix + body + sign-off) is fixed; subclasses supply the body. */
    public abstract static class SmsTemplate<E> {
        public final String render(E event) {
            return "FreshCart: " + body(event) + signOff();
        }

        protected abstract String body(E event);

        protected String signOff() {
            return "";
        }
    }

    static final SmsTemplate<OrderPlaced> PLACED = new SmsTemplate<>() {
        @Override
        protected String body(OrderPlaced e) {
            return "order " + e.orderId() + " confirmed, total " + e.total() + ".";
        }
    };

    static final SmsTemplate<OrderStatusChanged> STATUS = new SmsTemplate<>() {
        @Override
        protected String body(OrderStatusChanged e) {
            return "order " + e.orderId() + " is now " + e.status().replace('_', ' ').toLowerCase() + ".";
        }

        @Override
        protected String signOff() {
            return " Track: fc.example/t";
        }
    };

    private final Set<String> sent = new HashSet<>();

    public Notifications(DomainEvents events, Consumer<String> sms, Consumer<String> opsAlerts) {
        events.subscribe(OrderPlaced.class, e -> once(e.eventId(), () -> sms.accept(PLACED.render(e))));
        events.subscribe(OrderStatusChanged.class, e -> once(e.eventId(), () -> sms.accept(STATUS.render(e))));
        events.subscribe(StockLow.class, e -> opsAlerts.accept("restock " + e.sku() + " (" + e.left() + " left)"));
    }

    private void once(String eventId, Runnable send) {
        if (sent.add(eventId)) { // [Idempotent Consumer]: a redelivered event doesn't send a second SMS
            send.run();
        }
    }
}
