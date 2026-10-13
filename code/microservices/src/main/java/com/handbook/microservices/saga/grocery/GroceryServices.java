package com.handbook.microservices.saga.grocery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Three services, each with its own database and a local transaction per call. */
public final class GroceryServices {

    private GroceryServices() {
    }

    public static final class InventoryService {
        private final Map<String, Integer> stock = new HashMap<>(Map.of("MANGO-1KG", 10));

        public void reserve(String sku, int qty) {
            int available = stock.getOrDefault(sku, 0);
            if (available < qty) {
                throw new IllegalStateException("out of stock");
            }
            stock.put(sku, available - qty);
        }

        public void release(String sku, int qty) {
            stock.merge(sku, qty, Integer::sum);
        }

        public int available(String sku) {
            return stock.getOrDefault(sku, 0);
        }
    }

    public static final class PaymentService {
        private final Set<String> charged = new HashSet<>();

        public void charge(String orderId, long paise) {
            charged.add(orderId);
        }

        public void refund(String orderId) {
            charged.remove(orderId);
        }

        public boolean isCharged(String orderId) {
            return charged.contains(orderId);
        }
    }

    public static final class DeliveryService {
        private final Set<String> pincodesServed = Set.of("560001", "560034");

        public void schedule(String orderId, String pincode) {
            if (!pincodesServed.contains(pincode)) {
                throw new IllegalStateException("no delivery slots for " + pincode);
            }
        }
    }
}
