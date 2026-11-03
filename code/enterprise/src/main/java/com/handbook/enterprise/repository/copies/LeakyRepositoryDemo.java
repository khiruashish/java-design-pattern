package com.handbook.enterprise.repository.copies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Coding challenge: an in-memory repository must not let callers change stored state without save(). */
public class LeakyRepositoryDemo {

    static final class Cart { // mutable on purpose, like a JPA entity
        final String id;
        final List<String> items = new ArrayList<>();
        Cart(String id) { this.id = id; }
        Cart copy() {
            Cart c = new Cart(id);
            c.items.addAll(items);
            return c;
        }
    }

    static final class LeakyCartRepository {
        private final Map<String, Cart> store = new HashMap<>();
        void save(Cart c) { store.put(c.id, c); }
        Cart find(String id) { return store.get(id); } // hands out the stored object itself
    }

    static final class SafeCartRepository {
        private final Map<String, Cart> store = new HashMap<>();
        void save(Cart c) { store.put(c.id, c.copy()); } // copy in
        Cart find(String id) { return store.get(id).copy(); } // copy out
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: An In-Memory Repository That Behaves Like a Database ===");
        LeakyCartRepository leaky = new LeakyCartRepository();
        leaky.save(new Cart("C1"));
        leaky.find("C1").items.add("Mango"); // forgot to call save()
        System.out.println("Leaky repo after unsaved change: " + leaky.find("C1").items + "  <- tests pass, prod loses it");

        SafeCartRepository safe = new SafeCartRepository();
        safe.save(new Cart("C1"));
        safe.find("C1").items.add("Mango");
        System.out.println("Safe repo after unsaved change : " + safe.find("C1").items);
        Cart c = safe.find("C1");
        c.items.add("Mango");
        safe.save(c);
        System.out.println("Safe repo after save()         : " + safe.find("C1").items);
    }
}
