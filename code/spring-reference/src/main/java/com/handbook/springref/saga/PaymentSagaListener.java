package com.handbook.springref.saga;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * A choreography participant: payment-service reacts to StockReserved by charging the customer,
 * and publishes PaymentCompleted or PaymentFailed (which inventory-service uses to release stock).
 * Events are plain JSON strings here to keep the snippet short.
 */
@Component
public class PaymentSagaListener {

    private final KafkaTemplate<String, String> kafka;
    private final PaymentRepository payments;

    public PaymentSagaListener(KafkaTemplate<String, String> kafka, PaymentRepository payments) {
        this.kafka = kafka;
        this.payments = payments;
    }

    @KafkaListener(topics = "inventory.stock-reserved", groupId = "payment-service")
    @Transactional
    public void onStockReserved(String orderId) {
        if (payments.alreadyProcessed(orderId)) {
            return; // Kafka is at-least-once: make the handler idempotent
        }
        boolean charged = payments.charge(orderId);
        // In production, write this event to an outbox table in the same transaction instead.
        kafka.send(charged ? "payment.completed" : "payment.failed", orderId, orderId);
    }

    /** The local database access this listener needs. */
    public interface PaymentRepository {
        boolean alreadyProcessed(String orderId);

        boolean charge(String orderId);
    }
}
