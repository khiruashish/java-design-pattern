package com.handbook.microservices.eventsourcing.upcasting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpcastingDemo {

    public static void main(String[] args) {
        List<Map<String, Object>> stored = List.of(
                event("PriceSet", 1, "sku", "MANGO-1KG", "price", "399.00"),
                event("PriceSet", 1, "sku", "RICE-5KG", "price", "649.50"),
                event("PriceSet", 2, "sku", "MANGO-1KG", "amountPaise", 34_900L, "currency", "INR"));

        System.out.println("=== Challenge: upcasting old event versions on read ===");
        for (Map<String, Object> e : stored) {
            System.out.println("stored  " + e);
            System.out.println("  read  " + Upcaster.upcast(e));
        }
    }

    private static Map<String, Object> event(String type, int version, Object... kv) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("type", type);
        e.put("version", version);
        for (int i = 0; i < kv.length; i += 2) {
            e.put((String) kv[i], kv[i + 1]);
        }
        return e;
    }
}
