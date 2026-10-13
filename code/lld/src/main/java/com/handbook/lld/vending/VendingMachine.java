package com.handbook.lld.vending;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Vending machine LLD. Pattern: State. Each state decides how to react to the same events
 * (insert coin, select, cancel), which removes the big if/else on a status field.
 */
public final class VendingMachine {

    public record Product(String name, int priceRupees) {
    }

    sealed interface State permits Idle, HasMoney {
    }

    record Idle() implements State {
    }

    record HasMoney(int credit) implements State {
    }

    private static final int[] COINS = {10, 5, 2, 1};

    private final Map<String, Product> slots = new LinkedHashMap<>();
    private final Map<String, Integer> stock = new LinkedHashMap<>();
    private State state = new Idle();

    public VendingMachine load(String slot, Product product, int quantity) {
        slots.put(slot, product);
        stock.put(slot, quantity);
        return this;
    }

    public String insert(int coin) {
        if (coin != 1 && coin != 2 && coin != 5 && coin != 10) {
            return "rejected coin ₹" + coin;
        }
        state = switch (state) {
            case Idle i -> new HasMoney(coin);
            case HasMoney m -> new HasMoney(m.credit() + coin);
        };
        return "credit ₹" + ((HasMoney) state).credit();
    }

    public String select(String slot) {
        return switch (state) {
            case Idle i -> "insert coins first";
            case HasMoney m -> {
                Product p = slots.get(slot);
                if (p == null) {
                    yield "no such slot " + slot;
                }
                if (stock.get(slot) == 0) {
                    yield p.name() + " sold out, credit ₹" + m.credit() + " kept";
                }
                if (m.credit() < p.priceRupees()) {
                    yield p.name() + " costs ₹" + p.priceRupees() + ", insert ₹" + (p.priceRupees() - m.credit()) + " more";
                }
                stock.merge(slot, -1, Integer::sum);
                state = new Idle();
                yield "dispensed " + p.name() + change(m.credit() - p.priceRupees());
            }
        };
    }

    public String cancel() {
        if (state instanceof HasMoney m) {
            state = new Idle();
            return "refunded" + change(m.credit());
        }
        return "nothing to refund";
    }

    /** Greedy change-making works for canonical coin systems like 10/5/2/1. */
    private static String change(int amount) {
        if (amount == 0) {
            return "";
        }
        List<Integer> coins = new ArrayList<>();
        for (int c : COINS) {
            while (amount >= c) {
                coins.add(c);
                amount -= c;
            }
        }
        return ", change " + coins;
    }
}
