package com.handbook.microservices.decomposition.grocery;

import com.handbook.microservices.decomposition.grocery.GroceryServices.*;
import com.handbook.microservices.support.EventBus;

public class GroceryDecompositionDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Monolith Split by Business Capability ===");
        GroceryServices.ownership().forEach(System.out::println);
        EventBus bus = new EventBus(true);
        CatalogService catalog = new CatalogService(bus);
        InventoryService inventory = new InventoryService(bus);
        OrderService orders = new OrderService(inventory, bus);

        System.out.println("Customer 1 orders 2 mangoes:");
        System.out.println("  -> " + orders.place("MANGO-1KG", 2));
        System.out.println("Customer 2 orders 2 mangoes:");
        System.out.println("  -> " + orders.place("MANGO-1KG", 2));
        System.out.println("Customer 3 orders 1 mango:");
        System.out.println("  -> " + orders.place("MANGO-1KG", 1));
        System.out.println("Catalog page now shows: " + catalog.describe("MANGO-1KG"));
    }
}
