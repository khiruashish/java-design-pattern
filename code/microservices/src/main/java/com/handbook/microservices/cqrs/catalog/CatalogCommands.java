package com.handbook.microservices.cqrs.catalog;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The write side: validates commands against the current state and emits events.
 * It is optimised for correctness, not for queries; it can't even list products by category.
 */
public final class CatalogCommands {

    public sealed interface Event permits ProductAdded, PriceChanged {
    }

    public record ProductAdded(String sku, String name, String category, long pricePaise) implements Event {
    }

    public record PriceChanged(String sku, long oldPaise, long newPaise) implements Event {
    }

    private final Map<String, Long> prices = new HashMap<>();
    private final Consumer<Event> publish;

    public CatalogCommands(Consumer<Event> publish) {
        this.publish = publish;
    }

    public void addProduct(String sku, String name, String category, long pricePaise) {
        if (prices.containsKey(sku)) {
            throw new IllegalArgumentException(sku + " already exists");
        }
        prices.put(sku, pricePaise);
        publish.accept(new ProductAdded(sku, name, category, pricePaise));
    }

    public void changePrice(String sku, long newPaise) {
        Long old = prices.get(sku);
        if (old == null) {
            throw new IllegalArgumentException("unknown " + sku);
        }
        if (newPaise < old / 2) { // (1)!
            throw new IllegalArgumentException("price cut over 50% needs approval");
        }
        prices.put(sku, newPaise);
        publish.accept(new PriceChanged(sku, old, newPaise));
    }
}
