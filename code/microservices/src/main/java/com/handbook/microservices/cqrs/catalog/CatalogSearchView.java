package com.handbook.microservices.cqrs.catalog;

import com.handbook.microservices.cqrs.catalog.CatalogCommands.Event;
import com.handbook.microservices.cqrs.catalog.CatalogCommands.PriceChanged;
import com.handbook.microservices.cqrs.catalog.CatalogCommands.ProductAdded;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * The read side: a denormalised view shaped for "cheapest products in a category" (think an
 * Elasticsearch index or a Redis sorted set). It is updated asynchronously from events.
 */
public final class CatalogSearchView {

    public record Row(String sku, String name, String category, long pricePaise) {
    }

    private final Map<String, Row> rows = new HashMap<>();
    private final Queue<Event> inbox = new ArrayDeque<>();

    public void enqueue(Event event) { // (1)!
        inbox.add(event);
    }

    /** The projector: applies queued events. In production this runs continuously off Kafka. */
    public int catchUp() {
        int applied = 0;
        for (Event e; (e = inbox.poll()) != null; applied++) {
            switch (e) { // (2)!
                case ProductAdded a -> rows.put(a.sku(), new Row(a.sku(), a.name(), a.category(), a.pricePaise()));
                case PriceChanged p -> rows.computeIfPresent(p.sku(),
                        (k, r) -> new Row(r.sku(), r.name(), r.category(), p.newPaise()));
            }
        }
        return applied;
    }

    public List<String> cheapest(String category, int limit) { // (3)!
        return rows.values().stream()
                .filter(r -> r.category().equals(category))
                .sorted(Comparator.comparingLong(Row::pricePaise))
                .limit(limit)
                .map(r -> r.name() + " ₹" + r.pricePaise() / 100)
                .toList();
    }
}
