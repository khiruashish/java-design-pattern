package com.handbook.architecture.modular.shop;

import com.handbook.architecture.modular.shop.billing.BillingModule;
import com.handbook.architecture.modular.shop.catalog.CatalogApi;
import com.handbook.architecture.modular.shop.ordering.OrderingApi;
import com.handbook.architecture.modular.shop.shared.ModuleEvents;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModularShopDemo {

    public static void main(String[] args) {
        ModuleEvents events = new ModuleEvents();
        CatalogApi catalog = CatalogApi.create();
        OrderingApi ordering = OrderingApi.create(catalog, events);
        BillingModule billing = new BillingModule(events);

        System.out.println("=== Grocery: one deployable, three modules with enforced boundaries ===");
        Map<String, Integer> basket = new LinkedHashMap<>();
        basket.put("MANGO-1KG", 2);
        basket.put("RICE-5KG", 1);
        System.out.println("ordering.placeOrder -> " + ordering.placeOrder("CUST-42", basket));
        try {
            ordering.placeOrder("CUST-42", Map.of("OLD-SKU", 1));
        } catch (IllegalArgumentException e) {
            System.out.println("ordering.placeOrder -> rejected: " + e.getMessage());
        }
        System.out.println("billing invoices    -> " + billing.invoices());
        System.out.println("(new CatalogService() from the ordering package would not compile: it is package-private)");
    }
}
