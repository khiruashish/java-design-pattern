package com.handbook.microservices.eventsourcing.cart;

import com.handbook.microservices.eventsourcing.cart.CartEvents.CheckedOut;
import com.handbook.microservices.eventsourcing.cart.CartEvents.Event;
import com.handbook.microservices.eventsourcing.cart.CartEvents.ItemAdded;
import com.handbook.microservices.eventsourcing.cart.CartEvents.ItemRemoved;

import java.util.List;

public class CartHistoryDemo {

    public static void main(String[] args) {
        List<Event> log = List.of(
                new ItemAdded("C1", "MANGO-1KG"), new ItemAdded("C1", "AVOCADO-2"), new ItemRemoved("C1", "AVOCADO-2"),
                new CheckedOut("C1"),
                new ItemAdded("C2", "AVOCADO-2"), new ItemAdded("C2", "RICE-5KG"), new ItemRemoved("C2", "AVOCADO-2"),
                new CheckedOut("C2"),
                new ItemAdded("C3", "MILK-1L"), new ItemAdded("C3", "BREAD"), new ItemRemoved("C3", "BREAD"));

        System.out.println("=== E-commerce: what the cart table shows vs. what the events know ===");
        System.out.println("Current carts (CRUD view)          : " + CartEvents.currentCarts(log));
        System.out.println("Removed, yet the cart was bought   : " + CartEvents.removedBeforeCheckout(log));
        System.out.println("Insight: avocados are dropped at checkout. Is the price too high, or is stock unreliable?");
    }
}
