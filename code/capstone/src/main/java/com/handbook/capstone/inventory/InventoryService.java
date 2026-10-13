package com.handbook.capstone.inventory;

import com.handbook.capstone.cart.Cart;
import com.handbook.capstone.shared.DomainEvents;
import com.handbook.capstone.shared.Ids;

import java.util.LinkedHashMap;
import java.util.Map;

/** Inventory module: reservations with release (used as a saga step and its compensation). */
public final class InventoryService {

    private final Map<String, Integer> stock = new LinkedHashMap<>();
    private final DomainEvents events;
    private final int lowStockThreshold;

    public InventoryService(DomainEvents events, int lowStockThreshold) {
        this.events = events;
        this.lowStockThreshold = lowStockThreshold;
    }

    public InventoryService stock(String sku, int qty) {
        stock.put(sku, qty);
        return this;
    }

    public synchronized void reserve(Cart cart) {
        for (Cart.Line l : cart.orderedLines()) { // check everything first: all-or-nothing
            if (stock.getOrDefault(l.product().sku(), 0) < l.qty()) {
                throw new IllegalStateException("only " + stock.getOrDefault(l.product().sku(), 0) + " " + l.product().name() + " left");
            }
        }
        for (Cart.Line l : cart.orderedLines()) {
            int left = stock.merge(l.product().sku(), -l.qty(), Integer::sum);
            if (left <= lowStockThreshold) {
                events.publish(new DomainEvents.StockLow(Ids.INSTANCE.next("EVT"), l.product().sku(), left));
            }
        }
    }

    public synchronized void release(Cart cart) {
        cart.orderedLines().forEach(l -> stock.merge(l.product().sku(), l.qty(), Integer::sum));
    }

    public synchronized int available(String sku) {
        return stock.getOrDefault(sku, 0);
    }
}
