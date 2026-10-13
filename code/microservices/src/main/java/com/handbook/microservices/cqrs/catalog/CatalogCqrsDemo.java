package com.handbook.microservices.cqrs.catalog;

public class CatalogCqrsDemo {

    public static void main(String[] args) {
        var view = new CatalogSearchView();
        var commands = new CatalogCommands(view::enqueue);

        System.out.println("=== Grocery: catalog writes vs. search reads ===");
        commands.addProduct("MANGO-1KG", "Alphonso mangoes 1 kg", "fruit", 39_900);
        commands.addProduct("BANANA-12", "Robusta bananas x12", "fruit", 6_900);
        commands.addProduct("APPLE-1KG", "Shimla apples 1 kg", "fruit", 9_900);
        System.out.println("applied " + view.catchUp() + " events -> cheapest fruit: " + view.cheapest("fruit", 2));

        commands.changePrice("APPLE-1KG", 5_900); // apple season sale
        System.out.println("Price changed, projection not yet caught up -> " + view.cheapest("fruit", 2) + "  (stale read)");
        System.out.println("applied " + view.catchUp() + " event  -> cheapest fruit: " + view.cheapest("fruit", 2));

        try {
            commands.changePrice("MANGO-1KG", 9_900);
        } catch (IllegalArgumentException e) {
            System.out.println("Command rejected by the write model: " + e.getMessage());
        }
    }
}
