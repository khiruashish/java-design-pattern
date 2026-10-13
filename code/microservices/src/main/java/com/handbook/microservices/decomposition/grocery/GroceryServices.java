package com.handbook.microservices.decomposition.grocery;

import com.handbook.microservices.support.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Three services split by BUSINESS CAPABILITY. Each owns its data (a private map standing in
 * for its own database) and talks to the others only through APIs and events.
 */
public final class GroceryServices {

    private GroceryServices() {
    }

    public record OrderPlaced(String orderId, String sku, int qty) {
    }

    public record StockLow(String sku, int left) {
    }

    /** Catalog: what we sell, how it's described, whether to show it as available. */
    public static class CatalogService {
        private final Map<String, String> products = new HashMap<>(Map.of("MANGO-1KG", "Alphonso mangoes, 1 kg"));
        private final Map<String, Boolean> showAsAvailable = new HashMap<>(Map.of("MANGO-1KG", true));

        public CatalogService(EventBus bus) {
            bus.<StockLow>subscribe("inventory.stock-low", e -> { // (1)!
                showAsAvailable.put(e.sku(), e.left() > 0);
                System.out.println("  [catalog] " + e.sku() + " badge -> " + (e.left() > 0 ? "only " + e.left() + " left" : "out of stock"));
            });
        }

        public String describe(String sku) {
            return products.get(sku) + (showAsAvailable.get(sku) ? "" : " (out of stock)");
        }
    }

    /** Inventory: how many units each dark store physically has. */
    public static class InventoryService {
        private final Map<String, Integer> stock = new HashMap<>(Map.of("MANGO-1KG", 3));
        private final EventBus bus;

        public InventoryService(EventBus bus) {
            this.bus = bus;
            bus.<OrderPlaced>subscribe("orders.placed", e -> {
                int left = stock.merge(e.sku(), -e.qty(), Integer::sum);
                System.out.println("  [inventory] reserved " + e.qty() + " x " + e.sku() + ", " + left + " left");
                if (left <= 1) {
                    bus.publish("inventory.stock-low", new StockLow(e.sku(), left));
                }
            });
        }

        public boolean canFulfil(String sku, int qty) { // a synchronous API other services may call
            return stock.getOrDefault(sku, 0) >= qty;
        }
    }

    /** Ordering: the order lifecycle. It asks Inventory through its API, never reads its tables. */
    public static class OrderService {
        private final InventoryService inventoryApi;
        private final EventBus bus;
        private int next = 1;

        public OrderService(InventoryService inventoryApi, EventBus bus) {
            this.inventoryApi = inventoryApi;
            this.bus = bus;
        }

        public String place(String sku, int qty) {
            if (!inventoryApi.canFulfil(sku, qty)) { // (2)!
                return "rejected: not enough " + sku;
            }
            String id = "ORD-" + next++;
            bus.publish("orders.placed", new OrderPlaced(id, sku, qty)); // (3)!
            return "accepted " + id;
        }
    }

    public static List<String> ownership() {
        return List.of(
                "Catalog   owns: product names, descriptions, images, availability badge",
                "Inventory owns: units per dark store, reservations",
                "Ordering  owns: orders, order lifecycle");
    }
}
