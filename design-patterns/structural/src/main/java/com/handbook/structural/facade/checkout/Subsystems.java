package com.handbook.structural.facade.checkout;

import java.util.HashMap;
import java.util.Map;

/** The subsystems: each is a separate service with its own API. Kept in one file for the demo. */
public final class Subsystems {

    private Subsystems() {
    }

    public static class InventoryService {
        private final Map<String, Integer> stock = new HashMap<>(Map.of("MILK-1L", 10, "EGGS-12", 0, "BREAD-WW", 5));

        public boolean reserve(String sku, int qty) {
            int available = stock.getOrDefault(sku, 0);
            if (available < qty) {
                return false;
            }
            stock.put(sku, available - qty);
            System.out.println("  [inventory] reserved " + qty + " x " + sku);
            return true;
        }

        public void release(String sku, int qty) {
            stock.merge(sku, qty, Integer::sum);
            System.out.println("  [inventory] released " + qty + " x " + sku);
        }
    }

    public static class PaymentService {
        public String charge(String customerId, int amountInr) {
            if (amountInr > 5_000) {
                throw new IllegalStateException("card declined");
            }
            System.out.println("  [payment] charged INR " + amountInr + " to " + customerId);
            return "PAY-" + customerId + "-" + amountInr;
        }
    }

    public static class DeliveryService {
        public String scheduleSlot(String address) {
            System.out.println("  [delivery] slot booked 6-8 PM for " + address);
            return "SLOT-18-20";
        }
    }

    public static class NotificationService {
        public void send(String customerId, String message) {
            System.out.println("  [notify] " + customerId + ": " + message);
        }
    }
}
