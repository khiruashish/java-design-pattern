package com.handbook.structural.proxy.remote;

import java.util.Map;

/** The real subject, running in another process. Here it parses the wire format directly. */
public class InventoryServer implements InventoryApi {

    private final Map<String, Integer> stock = Map.of("BLR-1:RICE-5KG", 120, "BLR-1:OIL-1L", 0);
    private int calls;

    public int availableUnits(String warehouse, String sku) {
        return stock.getOrDefault(warehouse + ":" + sku, 0);
    }

    /** What the HTTP endpoint would do: decode, call, encode. */
    public String handle(String request) {
        calls++;
        String[] parts = request.split("\\|"); // "availableUnits|BLR-1|RICE-5KG"
        return "OK|" + availableUnits(parts[1], parts[2]);
    }

    public int calls() {
        return calls;
    }
}
