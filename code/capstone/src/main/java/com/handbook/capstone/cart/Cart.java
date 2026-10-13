package com.handbook.capstone.cart;

import com.handbook.capstone.catalog.Product;
import com.handbook.capstone.shared.Money;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [Command] Every change to the cart is a command object that knows how to undo itself, so the app
 * can offer "Undo" after an accidental removal.
 */
public final class Cart {

    public interface CartCommand {
        void execute(Cart cart);

        void undo(Cart cart);

        String describe();
    }

    public record AddItem(Product product, int qty) implements CartCommand {
        @Override
        public void execute(Cart cart) {
            cart.lines.merge(product.sku(), new Line(product, qty), (a, b) -> new Line(a.product(), a.qty() + b.qty()));
        }

        @Override
        public void undo(Cart cart) {
            cart.change(product, -qty);
        }

        @Override
        public String describe() {
            return "add " + qty + " x " + product.name();
        }
    }

    public record RemoveItem(Product product, int qty) implements CartCommand {
        @Override
        public void execute(Cart cart) {
            cart.change(product, -qty);
        }

        @Override
        public void undo(Cart cart) {
            new AddItem(product, qty).execute(cart);
        }

        @Override
        public String describe() {
            return "remove " + qty + " x " + product.name();
        }
    }

    public record Line(Product product, int qty) {
        public Money total() {
            return product.price().times(qty);
        }
    }

    private final String customerId;
    private final Map<String, Line> lines = new LinkedHashMap<>();
    private final Deque<CartCommand> history = new ArrayDeque<>();

    public Cart(String customerId) {
        this.customerId = customerId;
    }

    public void apply(CartCommand command) {
        command.execute(this);
        history.push(command);
    }

    public String undo() {
        if (history.isEmpty()) {
            return "nothing to undo";
        }
        CartCommand last = history.pop();
        last.undo(this);
        return "undid: " + last.describe();
    }

    private void change(Product p, int delta) {
        lines.computeIfPresent(p.sku(), (k, l) -> l.qty() + delta <= 0 ? null : new Line(p, l.qty() + delta));
    }

    public Map<String, Line> lines() {
        return Collections.unmodifiableMap(lines);
    }

    public Iterable<Line> orderedLines() {
        return lines.values();
    }

    public String customerId() {
        return customerId;
    }

    public Money subtotal() {
        return lines.values().stream().map(Line::total).reduce(Money.ZERO, Money::plus);
    }

    public int itemCount() {
        return lines.values().stream().mapToInt(Line::qty).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        lines.values().forEach(l -> sb.append(l.qty()).append(" x ").append(l.product().name()).append("; "));
        return sb.isEmpty() ? "(empty)" : sb.substring(0, sb.length() - 2);
    }
}
