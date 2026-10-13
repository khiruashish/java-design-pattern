package com.handbook.microservices.bff.ecommerce;

import java.util.List;

/** Downstream services. Each returns its full, general-purpose representation. */
public final class OrderBackends {

    private OrderBackends() {
    }

    public record Line(String sku, String name, int qty, long pricePaise) {
    }

    public record Order(String id, String customerId, List<Line> lines, String placedAt, String paymentRef) {
    }

    public record Shipment(String orderId, String carrier, String status, String eta, List<String> scans) {
    }

    public record ReviewPrompt(String sku, boolean eligible) {
    }

    public static final class OrderService {
        public Order get(String orderId) {
            return new Order(orderId, "CUST-42", List.of(
                    new Line("MANGO-1KG", "Alphonso mangoes 1 kg", 2, 39_900),
                    new Line("RICE-5KG", "Basmati rice 5 kg", 1, 64_900)),
                    "2026-10-12T09:15:00Z", "PAY-77812");
        }
    }

    public static final class ShippingService {
        public Shipment forOrder(String orderId) {
            return new Shipment(orderId, "QuickDrop", "OUT_FOR_DELIVERY", "18:30",
                    List.of("09:40 packed", "11:05 left hub", "16:10 out for delivery"));
        }
    }

    public static final class ReviewService {
        public List<ReviewPrompt> prompts(String customerId, List<String> skus) {
            return skus.stream().map(sku -> new ReviewPrompt(sku, sku.startsWith("MANGO"))).toList();
        }
    }
}
