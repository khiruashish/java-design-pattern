package com.handbook.architecture.modular.shop.ordering;

import com.handbook.architecture.modular.shop.catalog.CatalogApi;
import com.handbook.architecture.modular.shop.shared.ModuleEvents;

import java.util.Map;

/** The ordering module's public API. It depends on the catalog's API, never on its internals. */
public interface OrderingApi {

    String placeOrder(String customerId, Map<String, Integer> items);

    static OrderingApi create(CatalogApi catalog, ModuleEvents events) {
        return new OrderingService(catalog, events);
    }
}
