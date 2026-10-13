package com.handbook.microservices.saga.grocery;

import com.handbook.microservices.saga.core.SagaOrchestrator;
import com.handbook.microservices.saga.core.SagaOrchestrator.Outcome;
import com.handbook.microservices.saga.core.SagaOrchestrator.Step;
import com.handbook.microservices.saga.grocery.GroceryServices.DeliveryService;
import com.handbook.microservices.saga.grocery.GroceryServices.InventoryService;
import com.handbook.microservices.saga.grocery.GroceryServices.PaymentService;

import java.util.List;

/** The order service owns this saga and tells each participant what to do. */
public final class PlaceOrderSaga {

    public record Order(String id, String sku, int qty, long paise, String pincode) {
    }

    private final InventoryService inventory;
    private final PaymentService payments;
    private final DeliveryService delivery;

    public PlaceOrderSaga(InventoryService inventory, PaymentService payments, DeliveryService delivery) {
        this.inventory = inventory;
        this.payments = payments;
        this.delivery = delivery;
    }

    public Outcome place(Order o) {
        return new SagaOrchestrator(3).run(o.id(), List.of(
                Step.compensatable("reserve stock",
                        () -> inventory.reserve(o.sku(), o.qty()),
                        () -> inventory.release(o.sku(), o.qty())), // (1)!
                Step.compensatable("charge payment",
                        () -> payments.charge(o.id(), o.paise()),
                        () -> payments.refund(o.id())),
                Step.compensatable("schedule delivery",
                        () -> delivery.schedule(o.id(), o.pincode()),
                        () -> { })));
    }
}
