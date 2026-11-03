package com.handbook.enterprise.unitofwork.scope;

import com.handbook.enterprise.unitofwork.travel.IdentityMap;

import java.util.HashMap;
import java.util.Map;

/** Coding challenge: why an identity map must be scoped to one session/transaction, not global. */
public class ScopeDemo {

    record Price(String sku, long paise) { }

    static final Map<String, Long> PRICE_TABLE = new HashMap<>(Map.of("ONION-1KG", 4_000L));

    public static void main(String[] args) {
        System.out.println("=== Challenge: Identity Map Scope ===");
        IdentityMap<String, Price> global = new IdentityMap<>(sku -> new Price(sku, PRICE_TABLE.get(sku)));

        System.out.println("Request 1 (global map)     : " + global.get("ONION-1KG"));
        PRICE_TABLE.put("ONION-1KG", 9_000L); // pricing team updates after a supply shock
        System.out.println("Request 2 (global map)     : " + global.get("ONION-1KG") + "  <- stale forever");

        IdentityMap<String, Price> perRequest = new IdentityMap<>(sku -> new Price(sku, PRICE_TABLE.get(sku)));
        System.out.println("Request 2 (per-request map): " + perRequest.get("ONION-1KG"));
        System.out.println("An identity map guarantees identity, not freshness: scope it to one unit of work.");
    }
}
