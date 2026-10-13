package com.handbook.architecture.modular.shop.ordering;

import com.handbook.architecture.modular.shop.catalog.CatalogApi;
import com.handbook.architecture.modular.shop.shared.ModuleEvents;
import com.handbook.architecture.modular.shop.shared.OrderPlaced;

import java.util.Map;

class OrderingService implements OrderingApi {

    private final CatalogApi catalog;
    private final ModuleEvents events;
    private int nextId = 1;

    OrderingService(CatalogApi catalog, ModuleEvents events) {
        this.catalog = catalog;
        this.events = events;
    }

    @Override
    public String placeOrder(String customerId, Map<String, Integer> items) {
        long total = 0;
        for (var item : items.entrySet()) {
            long price = catalog.priceOf(item.getKey()) // (1)!
                    .orElseThrow(() -> new IllegalArgumentException("not for sale: " + item.getKey()));
            total += price * item.getValue();
        }
        String id = "ORD-" + nextId++;
        events.publish(new OrderPlaced(id, customerId, total)); // (2)!
        return id;
    }
}
