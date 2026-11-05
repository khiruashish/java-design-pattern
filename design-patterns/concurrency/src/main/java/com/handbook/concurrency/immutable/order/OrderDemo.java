package com.handbook.concurrency.immutable.order;

import com.handbook.concurrency.immutable.order.OrderSnapshot.Line;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OrderDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== E-commerce: Order Snapshot Shared by Four Services ===");
        List<Line> cart = new ArrayList<>(List.of(new Line("PHONE-CASE", 1, 499), new Line("CHARGER-65W", 1, 1_899)));
        OrderSnapshot snapshot = new OrderSnapshot("ORD-77", Instant.parse("2026-10-13T10:15:30Z"), cart);

        cart.add(new Line("SNEAKY-ADDON", 1, 9_999)); // the cart UI keeps changing after checkout...
        System.out.println("Cart changed after checkout; snapshot total still INR " + snapshot.totalInr());

        try {
            snapshot.lines().add(new Line("HACK", 1, 1));
        } catch (UnsupportedOperationException e) {
            System.out.println("Snapshot lines cannot be modified");
        }

        try (ExecutorService services = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> results = List.of(
                    services.submit(() -> "billing  : invoice INR " + snapshot.totalInr()),
                    services.submit(() -> "warehouse: pick " + snapshot.lines().size() + " lines"),
                    services.submit(() -> "email    : receipt for " + snapshot.orderId()),
                    services.submit(() -> "analytics: revenue " + snapshot.totalInr()));
            for (Future<String> f : results) {
                System.out.println(f.get()); // no locks: nothing can change
            }
        }

        OrderSnapshot amended = snapshot.withLine(new Line("SCREEN-GUARD", 1, 299));
        System.out.println("Amended copy total INR " + amended.totalInr() + ", original still INR " + snapshot.totalInr());
    }
}
