package com.handbook.capstone;

import com.handbook.capstone.cart.Cart;
import com.handbook.capstone.checkout.CheckoutChecks;
import com.handbook.capstone.delivery.DeliveryService;
import com.handbook.capstone.inventory.InventoryService;
import com.handbook.capstone.ordering.CheckoutSaga;
import com.handbook.capstone.ordering.OrderState;
import com.handbook.capstone.ordering.OrderStore;
import com.handbook.capstone.ordering.OrderStore.OrderRow;
import com.handbook.capstone.ordering.TokenBucket;
import com.handbook.capstone.payment.Payments;
import com.handbook.capstone.pricing.Pricing;
import com.handbook.capstone.shared.DomainEvents;
import com.handbook.capstone.shared.Ids;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * [Facade] The single entry point the app's API layer calls. It hides eight modules and the order
 * in which they must be used behind three methods: checkout, advance, relayEvents.
 */
public final class FreshCart {

    public record CheckoutResult(int status, String orderId, String message) {
        @Override
        public String toString() {
            return status + " " + (orderId == null ? "" : orderId + " ") + message;
        }
    }

    private final Pricing pricing;
    private final CheckoutChecks checks;
    private final InventoryService inventory;
    private final DeliveryService delivery;
    private final DeliveryService.SlotStrategy slotStrategy;
    private final Payments.GatewayFactory payments;
    private final OrderStore store;
    private final TokenBucket limiter;
    private final DomainEvents events;
    private final CheckoutSaga saga;

    public FreshCart(Pricing pricing, CheckoutChecks checks, InventoryService inventory, DeliveryService delivery,
                     DeliveryService.SlotStrategy slotStrategy, Payments.GatewayFactory payments, OrderStore store,
                     TokenBucket limiter, DomainEvents events, Consumer<String> sagaLog) {
        this.pricing = pricing;
        this.checks = checks;
        this.inventory = inventory;
        this.delivery = delivery;
        this.slotStrategy = slotStrategy;
        this.payments = payments;
        this.store = store;
        this.limiter = limiter;
        this.events = events;
        this.saga = new CheckoutSaga(sagaLog);
    }

    public CheckoutResult checkout(Cart cart, String pincode, String coupon, String paymentMethod, String idempotencyKey) {
        Optional<OrderRow> existing = store.byIdempotencyKey(idempotencyKey); // (1)!
        if (existing.isPresent()) {
            return new CheckoutResult(200, existing.get().id(), "already placed (idempotent replay), no new charge");
        }
        if (!limiter.tryAcquire(cart.customerId())) { // (2)!
            return new CheckoutResult(429, null, "too many checkout attempts, try again in a minute");
        }
        Optional<String> problem = checks.check(new CheckoutChecks.Request(cart, pincode)); // (3)!
        if (problem.isPresent()) {
            return new CheckoutResult(422, null, problem.get());
        }
        Pricing.Breakdown price = pricing.price(cart, coupon);
        String orderId = Ids.INSTANCE.next("ORD");
        Payments.PaymentGateway gateway = payments.forMethod(paymentMethod); // (4)!
        DeliveryService.Slot[] slot = new DeliveryService.Slot[1];
        try {
            saga.run(List.of( // (5)!
                    new CheckoutSaga.Step("reserve stock", () -> inventory.reserve(cart), () -> inventory.release(cart)),
                    new CheckoutSaga.Step("book delivery slot", () -> slot[0] = delivery.book(slotStrategy),
                            () -> delivery.cancel(slot[0])),
                    new CheckoutSaga.Step("charge " + price.total() + " via " + paymentMethod, () -> {
                        Payments.Result r = gateway.charge(new Payments.Charge(orderId, cart.customerId(), price.total()));
                        if (!(r instanceof Payments.Paid)) {
                            throw new IllegalStateException(r.toString());
                        }
                    }, () -> gateway.refund(orderId)),
                    new CheckoutSaga.Step("save order + outbox event", () -> store.insert( // (6)!
                            new OrderRow(orderId, cart.customerId(), price.total(), OrderState.CONFIRMED, idempotencyKey),
                            Ids.INSTANCE.next("EVT")), () -> { })));
        } catch (CheckoutSaga.Failed f) {
            return new CheckoutResult(402, null, "checkout failed: " + f.getMessage());
        }
        return new CheckoutResult(201, orderId, price.total() + ", delivery at " + slot[0].start());
    }

    /** Moves an order through its lifecycle; the state object decides what's legal. */
    public String advance(String orderId, String action) {
        OrderRow order = store.byId(orderId).orElseThrow();
        try {
            OrderState next = order.state().on(action); // (7)!
            store.updateState(order, next, Ids.INSTANCE.next("EVT"));
            return orderId + " " + order.state() + " -> " + next;
        } catch (IllegalStateException e) {
            return orderId + " rejected: " + e.getMessage();
        }
    }

    /** Runs the outbox relay once (a scheduler does this every few hundred ms in production). */
    public int relayEvents() {
        return store.relay(events);
    }
}
