package com.handbook.capstone;

import com.handbook.capstone.cart.Cart;
import com.handbook.capstone.catalog.Category;
import com.handbook.capstone.catalog.Product;
import com.handbook.capstone.checkout.CheckoutChecks;
import com.handbook.capstone.delivery.DeliveryService;
import com.handbook.capstone.inventory.InventoryService;
import com.handbook.capstone.loyalty.LoyaltyProgram;
import com.handbook.capstone.ordering.OrderHistoryView;
import com.handbook.capstone.ordering.OrderState;
import com.handbook.capstone.ordering.OrderStore;
import com.handbook.capstone.ordering.TokenBucket;
import com.handbook.capstone.payment.Payments;
import com.handbook.capstone.pricing.Pricing;
import com.handbook.capstone.shared.DomainEvents;
import com.handbook.capstone.shared.Money;
import com.handbook.capstone.shared.MutableClock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FreshCartTest {

    private final MutableClock clock = new MutableClock(Instant.parse("2026-01-01T10:00:00Z"));
    private final DomainEvents events = new DomainEvents();
    private final InventoryService inventory = new InventoryService(events, 0).stock("A", 5);
    private final OrderStore store = new OrderStore();
    private final Product a = Product.builder("A").name("Apple").category(Category.of("fruit", 0, true))
            .price(Money.rupees(100)).build();
    private boolean declineAll;

    private FreshCart app() {
        Payments.PaymentGateway card = new Payments.PaymentGateway() {
            @Override
            public Payments.Result charge(Payments.Charge c) {
                return declineAll ? new Payments.Declined("no") : new Payments.Paid("T");
            }

            @Override
            public void refund(String reference) {
            }
        };
        return new FreshCart(new Pricing(Money.rupees(0), Money.ZERO), CheckoutChecks.standardChain(Set.of("1")),
                inventory, new DeliveryService().slot(LocalTime.NOON, 10, false), DeliveryService.earliest(),
                new Payments.GatewayFactory(Map.of("CARD", card)), store, new TokenBucket(100, 1, clock), events, s -> { });
    }

    private Cart cartWith(int qty) {
        Cart c = new Cart("C1");
        c.apply(new Cart.AddItem(a, qty));
        return c;
    }

    @Test
    void successfulCheckoutStoresOrderAndOutboxEventAtomically() {
        var r = app().checkout(cartWith(2), "1", "", "CARD", "k1");
        assertThat(r.status()).isEqualTo(201);
        assertThat(store.count("orders")).isEqualTo(1);
        assertThat(store.count("outbox")).isEqualTo(1);
        assertThat(inventory.available("A")).isEqualTo(3);
    }

    @Test
    void sameIdempotencyKeyNeverCreatesASecondOrder() {
        var app = app();
        String first = app.checkout(cartWith(1), "1", "", "CARD", "k1").orderId();
        var again = app.checkout(cartWith(1), "1", "", "CARD", "k1");
        assertThat(again.orderId()).isEqualTo(first);
        assertThat(store.count("orders")).isEqualTo(1);
    }

    @Test
    void declinedPaymentCompensatesStock() {
        declineAll = true;
        assertThat(app().checkout(cartWith(2), "1", "", "CARD", "k1").status()).isEqualTo(402);
        assertThat(inventory.available("A")).isEqualTo(5);
        assertThat(store.count("orders")).isZero();
    }

    @Test
    void relayedEventsFeedReadModelAndLoyaltyExactlyOnce() {
        var history = new OrderHistoryView(events);
        var loyalty = new LoyaltyProgram(events);
        var app = app();
        app.checkout(cartWith(3), "1", "", "CARD", "k1");
        app.relayEvents();
        store.simulateRelayCrashBeforeMarking();
        app.relayEvents();
        assertThat(history.myOrders("C1")).hasSize(1);
        assertThat(loyalty.points("C1")).isEqualTo(3);
        assertThat(loyalty.duplicatesIgnored()).isEqualTo(1);
    }

    @Test
    void stateMachineRejectsIllegalTransitions() {
        assertThat(OrderState.CONFIRMED.on("pack")).isEqualTo(OrderState.PACKED);
        assertThatThrownBy(() -> OrderState.DELIVERED.on("cancel")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void undoRestoresRemovedItems() {
        Cart c = cartWith(2);
        c.apply(new Cart.RemoveItem(a, 2));
        assertThat(c.itemCount()).isZero();
        c.undo();
        assertThat(c.itemCount()).isEqualTo(2);
    }
}
