package com.handbook.testing.doubles.overmocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Coding challenge: an interaction-heavy test breaks on a harmless refactor; a state-based test with a fake doesn't. */
public class OverMockingDemo {

    interface StockStore {
        void save(String sku, int qty);
        void saveAll(Map<String, Integer> batch);
    }

    /** Records calls (like a strict mock) and also keeps state (like a fake). */
    static final class RecordingStore implements StockStore {
        final List<String> calls = new ArrayList<>();
        final Map<String, Integer> state = new HashMap<>();

        public void save(String sku, int qty) {
            calls.add("save(" + sku + ")");
            state.put(sku, qty);
        }

        public void saveAll(Map<String, Integer> batch) {
            calls.add("saveAll(" + batch.size() + ")");
            state.putAll(batch);
        }
    }

    /** Version 1: one save per SKU. */
    static void restockV1(StockStore store) {
        store.save("MILK", 50);
        store.save("EGGS", 30);
    }

    /** Version 2 (refactor for performance): one batched call. Same behaviour. */
    static void restockV2(StockStore store) {
        Map<String, Integer> batch = new java.util.TreeMap<>(Map.of("MILK", 50, "EGGS", 30));
        store.saveAll(batch);
    }


    public static void main(String[] args) {
        System.out.println("=== Challenge: Over-Mocking vs State-Based Tests ===");
        for (String version : new String[] {"v1", "v2"}) {
            RecordingStore store = new RecordingStore();
            if (version.equals("v1")) {
                restockV1(store);
            } else {
                restockV2(store);
            }
            boolean interactionPass = store.calls.equals(List.of("save(MILK)", "save(EGGS)")); // verify(store).save(...) x2
            boolean statePass = store.state.equals(Map.of("MILK", 50, "EGGS", 30));             // assert the outcome
            System.out.printf("%s calls=%s -> interaction test %s, state test %s%n", version, store.calls,
                    interactionPass ? "PASS" : "FAIL", statePass ? "PASS" : "FAIL");
        }
        System.out.println("Assert on outcomes with fakes; reserve mocks for side effects you can only observe as calls.");
    }
}
