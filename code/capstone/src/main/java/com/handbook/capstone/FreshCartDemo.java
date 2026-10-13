package com.handbook.capstone;

import com.handbook.capstone.cart.Cart;
import com.handbook.capstone.catalog.Catalog;
import com.handbook.capstone.catalog.Category;
import com.handbook.capstone.catalog.Product;
import com.handbook.capstone.checkout.CheckoutChecks;
import com.handbook.capstone.delivery.DeliveryService;
import com.handbook.capstone.inventory.InventoryService;
import com.handbook.capstone.loyalty.LoyaltyProgram;
import com.handbook.capstone.notification.Notifications;
import com.handbook.capstone.ordering.OrderHistoryView;
import com.handbook.capstone.ordering.OrderStore;
import com.handbook.capstone.ordering.TokenBucket;
import com.handbook.capstone.payment.Payments;
import com.handbook.capstone.pricing.Pricing;
import com.handbook.capstone.shared.DomainEvents;
import com.handbook.capstone.shared.Money;
import com.handbook.capstone.shared.MutableClock;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public class FreshCartDemo {

    public static void main(String[] args) {
        MutableClock clock = new MutableClock(Instant.parse("2026-10-14T17:00:00Z"));
        DomainEvents events = new DomainEvents();

        System.out.println("=== FreshCart: a grocery platform built from the handbook's patterns ===");

        // ---------- Catalog: Builder, Flyweight, Proxy ----------
        System.out.println("\n1) Catalog [Builder] [Flyweight] [Proxy]");
        Category fruit = Category.of("fruit", 0, true);
        Category staples = Category.of("staples", 5, false);
        Category dairy = Category.of("dairy", 5, true);
        var db = new Catalog.InMemoryCatalog()
                .add(Product.builder("MANGO-1KG").name("Alphonso mangoes 1 kg").category(fruit).price(Money.rupees(399)).maxPerOrder(5).organic().build())
                .add(Product.builder("RICE-5KG").name("Basmati rice 5 kg").category(staples).price(Money.rupees(649)).build())
                .add(Product.builder("MILK-1L").name("Toned milk 1 L").category(Category.of("dairy", 5, true)).price(Money.rupees(56)).build())
                .add(Product.builder("GHEE-1L").name("Cow ghee 1 L").category(dairy).price(Money.rupees(750)).build());
        Catalog catalog = new Catalog.CachingCatalog(db);
        for (int i = 0; i < 3; i++) {
            catalog.find("MANGO-1KG");
            catalog.find("RICE-5KG");
        }
        System.out.println("   6 lookups -> " + db.reads() + " database reads (caching proxy)");
        System.out.println("   4 products share " + Category.cachedInstances() + " category objects (flyweight)");
        try {
            Product.builder("BAD-1").name("No price").category(fruit).build();
        } catch (IllegalStateException e) {
            System.out.println("   builder validation: " + e.getMessage());
        }
        Product mango = catalog.find("MANGO-1KG").orElseThrow();
        Product rice = catalog.find("RICE-5KG").orElseThrow();
        Product milk = catalog.find("MILK-1L").orElseThrow();
        Product ghee = catalog.find("GHEE-1L").orElseThrow();

        // ---------- Cart: Command with undo ----------
        System.out.println("\n2) Cart [Command]");
        Cart asha = new Cart("CUST-42");
        asha.apply(new Cart.AddItem(mango, 2));
        asha.apply(new Cart.AddItem(rice, 1));
        asha.apply(new Cart.AddItem(milk, 3));
        asha.apply(new Cart.RemoveItem(milk, 3));
        System.out.println("   after removing milk: " + asha);
        System.out.println("   " + asha.undo());
        System.out.println("   cart: " + asha + " | subtotal " + asha.subtotal());

        // ---------- Pricing: Specification + Strategy ----------
        System.out.println("\n3) Pricing [Specification] [Strategy]");
        Pricing pricing = new Pricing(Money.rupees(500), Money.rupees(30))
                .add(new Pricing.Promotion("FRUIT10", Pricing.containsCategory("fruit"),
                        Pricing.percentOffCategory("fruit", 10, Money.rupees(100))))
                .add(new Pricing.Promotion("FLAT75", Pricing.minSubtotal(Money.rupees(1_000)), Pricing.flat(Money.rupees(75))));
        System.out.println("   FRUIT10 -> " + pricing.price(asha, "FRUIT10"));
        System.out.println("   FLAT75  -> " + pricing.price(asha, "FLAT75"));

        // ---------- Wiring the rest (composition root) ----------
        var inventory = new InventoryService(events, 3).stock("MANGO-1KG", 5).stock("RICE-5KG", 20)
                .stock("MILK-1L", 50).stock("GHEE-1L", 40);
        var delivery = new DeliveryService().slot(LocalTime.of(18, 0), 2, false).slot(LocalTime.of(19, 0), 10, true);
        int[] payFastCalls = {0};
        boolean[] payFastDown = {false};
        Payments.PayFastSdk payFast = new Payments.PayFastSdk() {
            @Override
            public Map<String, String> createPayment(String merchantKey, String amountRupees, String orderRef) {
                payFastCalls[0]++;
                if (payFastDown[0]) {
                    throw new IllegalStateException("PayFast timeout");
                }
                return Double.parseDouble(amountRupees) > 5_000
                        ? Map.of("status", "DECLINED", "reason", "card limit exceeded")
                        : Map.of("status", "CAPTURED", "txn", "PF-" + orderRef);
            }

            @Override
            public void reverse(String orderRef) {
                System.out.println("      PayFast reversal for " + orderRef);
            }
        };
        var upi = new Payments.CircuitBreakerGateway(new Payments.PayFastAdapter(payFast), 2, Duration.ofSeconds(30), clock);
        var payments = new Payments.GatewayFactory(Map.of("UPI", upi, "COD", new Payments.CashOnDelivery()));
        OrderStore store = new OrderStore();
        OrderHistoryView history = new OrderHistoryView(events);
        LoyaltyProgram loyalty = new LoyaltyProgram(events);
        new Notifications(events, sms -> System.out.println("      SMS: " + sms), alert -> System.out.println("      OPS: " + alert));
        FreshCart app = new FreshCart(pricing, CheckoutChecks.standardChain(Set.of("560001", "560034")), inventory, delivery,
                DeliveryService.ecoFirst(), payments, store, new TokenBucket(4, 1.0 / 60, clock), events,
                line -> System.out.println("      [Saga] " + line));

        // ---------- Happy path ----------
        System.out.println("\n4) Checkout [Facade] [Chain of Responsibility] [Saga] [Factory Method] [Adapter] [Circuit Breaker] [Outbox]");
        var first = app.checkout(asha, "560034", "FRUIT10", "UPI", "key-asha-1");
        System.out.println("   -> " + first);
        System.out.println("   orders table: " + store.count("orders") + ", outbox rows: " + store.count("outbox"));

        System.out.println("\n5) Outbox relay -> events [Observer] [CQRS] [Idempotent Consumer]");
        System.out.println("   relayed " + app.relayEvents() + " event(s)");
        System.out.println("   loyalty points CUST-42: " + loyalty.points("CUST-42"));
        System.out.println("   My orders (read model): " + history.myOrders("CUST-42"));

        System.out.println("\n6) Double-tap on 'Pay' (same idempotency key)");
        int callsBefore = payFastCalls[0];
        System.out.println("   -> " + app.checkout(asha, "560034", "FRUIT10", "UPI", "key-asha-1"));
        System.out.println("   PayFast calls for the retry: " + (payFastCalls[0] - callsBefore));

        // ---------- Failures ----------
        System.out.println("\n7) Validation chain rejects");
        Cart ravi = new Cart("CUST-77");
        ravi.apply(new Cart.AddItem(milk, 2));
        System.out.println("   pincode 110001 -> " + app.checkout(ravi, "110001", "", "UPI", "key-ravi-1"));
        System.out.println("   empty cart     -> " + app.checkout(new Cart("CUST-88"), "560001", "", "UPI", "key-x-1"));

        System.out.println("\n8) Payment declined -> saga compensates");
        Cart big = new Cart("CUST-77");
        big.apply(new Cart.AddItem(ghee, 8));
        System.out.println("   -> " + app.checkout(big, "560001", "", "UPI", "key-ravi-2"));
        System.out.println("   ghee stock after compensation: " + inventory.available("GHEE-1L") + " (unchanged)");

        System.out.println("\n9) PayFast outage -> circuit breaker opens, COD still works");
        payFastDown[0] = true;
        Cart meera = new Cart("CUST-55");
        meera.apply(new Cart.AddItem(milk, 2));
        for (int i = 1; i <= 3; i++) {
            int before = payFastCalls[0];
            var r = app.checkout(meera, "560001", "", "UPI", "key-meera-" + i);
            System.out.println("   attempt " + i + " -> " + r + " (PayFast called: " + (payFastCalls[0] > before) + ")");
            clock.advance(Duration.ofSeconds(10));
        }
        System.out.println("   breaker open: " + upi.isOpen());
        System.out.println("   pay by COD -> " + app.checkout(meera, "560001", "", "COD", "key-meera-cod"));

        // ---------- Lifecycle ----------
        System.out.println("\n10) Order lifecycle [State]");
        String orderId = first.orderId();
        System.out.println("   " + app.advance(orderId, "pack"));
        System.out.println("   " + app.advance(orderId, "dispatch"));
        System.out.println("   " + app.advance(orderId, "cancel"));
        System.out.println("   " + app.advance(orderId, "deliver"));
        System.out.println("   relayed " + app.relayEvents() + " event(s)");
        System.out.println("   My orders (read model): " + history.myOrders("CUST-42"));

        System.out.println("\n11) A script hammers checkout [Rate Limiter]");
        Cart bot = new Cart("CUST-99");
        bot.apply(new Cart.AddItem(rice, 1));
        for (int i = 1; i <= 5; i++) {
            System.out.println("   attempt " + i + " -> " + app.checkout(bot, "560099", "", "COD", "key-bot-" + i));
        }

        System.out.println("\n12) Relay crashes before marking -> events redelivered");
        store.simulateRelayCrashBeforeMarking();
        System.out.println("   relayed " + app.relayEvents() + " event(s) again");
        System.out.println("   loyalty duplicates ignored: " + loyalty.duplicatesIgnored() + ", points still " + loyalty.points("CUST-42"));

        System.out.println("\nPatterns used: Singleton, Builder, Flyweight, Proxy, Command, Specification, Strategy,"
                + " Chain of Responsibility, Facade, Factory Method, Adapter, Decorator, Circuit Breaker, Saga,"
                + " Transactional Outbox, Observer, CQRS, Idempotent Consumer, Template Method, State, Rate Limiter,"
                + " Modular Monolith");
    }
}
