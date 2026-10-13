package com.handbook.microservices.eventsourcing.upcasting;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Coding challenge: stored events are immutable, but schemas evolve. Version 1 of PriceSet stored
 * the price as a rupee string ("399.00"); version 2 stores integer paise and a currency.
 * An upcaster converts old events on read, so the domain code only ever sees the latest version.
 */
public final class Upcaster {

    private Upcaster() {
    }

    public static Map<String, Object> upcast(Map<String, Object> stored) {
        Map<String, Object> e = new LinkedHashMap<>(stored);
        if ("PriceSet".equals(e.get("type")) && Integer.valueOf(1).equals(e.get("version"))) {
            long paise = new BigDecimal((String) e.remove("price")).movePointRight(2).longValueExact();
            e.put("version", 2);
            e.put("amountPaise", paise);
            e.put("currency", "INR"); // v1 only ever existed in the Indian store
        }
        return e;
    }
}
