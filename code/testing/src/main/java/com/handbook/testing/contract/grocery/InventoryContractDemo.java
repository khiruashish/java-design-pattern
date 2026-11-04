package com.handbook.testing.contract.grocery;

import com.handbook.testing.contract.core.Contract;
import com.handbook.testing.contract.core.Contract.AnyInteger;
import com.handbook.testing.contract.core.Contract.AnyString;
import com.handbook.testing.contract.core.Contract.Interaction;
import com.handbook.testing.contract.core.Contract.Regex;
import com.handbook.testing.contract.core.Contract.Response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class InventoryContractDemo {

    static final Map<String, Integer> STOCK = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("=== Grocery: Order Service's Contract with the Inventory Service ===");
        Contract contract = new Contract("order-service", "inventory-service") // written by the ORDER team
                .uponReceiving(new Interaction("stock for a known SKU", "SKU MILK-1L has stock",
                        "/stock/MILK-1L", 200, Map.of("sku", new AnyString(), "available", new AnyInteger(),
                        "updatedAt", new Regex("\\d{4}-\\d{2}-\\d{2}T.*Z"))))
                .uponReceiving(new Interaction("unknown SKU", "SKU NOPE does not exist", "/stock/NOPE", 404, Map.of()));

        System.out.println("Provider v1.8 (current):");
        contract.verify(InventoryContractDemo::state, provider("available")).forEach(l -> System.out.println("  " + l));
        System.out.println("Provider v1.9 (renames 'available' to 'availableQty' in a 'harmless cleanup'):");
        contract.verify(InventoryContractDemo::state, provider("availableQty")).forEach(l -> System.out.println("  " + l));
        System.out.println("v1.9 fails in the INVENTORY team's CI, before it can break order placement in production.");
    }

    static void state(String providerState) { // provider states set up the data each interaction needs
        STOCK.clear();
        if (providerState.equals("SKU MILK-1L has stock")) {
            STOCK.put("MILK-1L", 37);
        }
    }

    static Function<String, Response> provider(String quantityField) {
        return path -> {
            String sku = path.substring("/stock/".length());
            Integer qty = STOCK.get(sku);
            if (qty == null) {
                return new Response(404, Map.of("error", "unknown sku"));
            }
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("sku", sku);
            body.put(quantityField, qty);
            body.put("updatedAt", "2026-10-02T09:15:00Z");
            return new Response(200, body);
        };
    }
}
