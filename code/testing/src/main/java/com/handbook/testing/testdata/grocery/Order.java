package com.handbook.testing.testdata.grocery;

import java.util.List;

/** Production type with many fields, most irrelevant to any single test. */
public record Order(String id, String customerId, String pincode, String slot, String paymentMethod,
                    boolean expressDelivery, boolean firstOrder, List<Line> lines, String couponCode) {

    public record Line(String sku, int qty, long unitPaise) { }

    public long subtotalPaise() {
        return lines.stream().mapToLong(l -> l.qty() * l.unitPaise()).sum();
    }

    /** The rule under test: delivery fee. */
    public long deliveryFeePaise() {
        if (firstOrder) {
            return 0;
        }
        long base = subtotalPaise() >= 49_900 ? 0 : 2_500;
        return base + (expressDelivery ? 3_000 : 0);
    }
}
