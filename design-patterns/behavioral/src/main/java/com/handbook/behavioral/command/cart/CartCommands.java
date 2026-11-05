package com.handbook.behavioral.command.cart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Challenge solution: "Reorder my usual" as a macro command with all-or-nothing undo. */
public final class CartCommands {

    private CartCommands() {
    }

    public static class Cart {
        private final Map<String, Integer> lines = new LinkedHashMap<>();

        void add(String sku, int qty) { lines.merge(sku, qty, Integer::sum); }

        void remove(String sku, int qty) {
            lines.computeIfPresent(sku, (k, v) -> v - qty <= 0 ? null : v - qty);
        }

        @Override
        public String toString() { return lines.toString(); }
    }

    public interface Command {
        void execute();
        void undo();
    }

    public record AddItem(Cart cart, String sku, int qty) implements Command {
        public void execute() { cart.add(sku, qty); }
        public void undo() { cart.remove(sku, qty); }
    }

    public static final class Macro implements Command {
        private final List<Command> steps;

        public Macro(List<Command> steps) {
            this.steps = List.copyOf(steps);
        }

        public void execute() {
            steps.forEach(Command::execute);
        }

        public void undo() {
            List<Command> reversed = new ArrayList<>(steps);
            java.util.Collections.reverse(reversed);
            reversed.forEach(Command::undo); // undo in reverse order
        }
    }
}
