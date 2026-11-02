package com.handbook.principles.solid.srp.before;

import com.handbook.principles.solid.srp.LineItem;

import java.util.List;
import java.util.Map;

/**
 * VIOLATES SRP: this class has three reasons to change.
 *   1. the tax rule changes          (business / finance team)
 *   2. the printed layout changes    (UI / reporting team)
 *   3. the storage technology changes (infrastructure team)
 */
public class Invoice {
    private final String number;
    private final List<LineItem> items;

    public Invoice(String number, List<LineItem> items) {
        this.number = number;
        this.items = List.copyOf(items);
    }

    // Responsibility 1: business calculation
    public long totalCents() {
        long subtotal = items.stream().mapToLong(LineItem::totalCents).sum();
        return subtotal + subtotal * 18 / 100;              // 18% tax hard-coded here
    }

    // Responsibility 2: presentation
    public String print() {
        StringBuilder sb = new StringBuilder("INVOICE " + number + "\n");
        for (LineItem i : items) {
            sb.append(String.format("  %-10s x%d  %8.2f%n", i.description(), i.quantity(), i.totalCents() / 100.0));
        }
        return sb.append(String.format("  TOTAL (incl. tax) %8.2f", totalCents() / 100.0)).toString();
    }

    // Responsibility 3: persistence
    public void save(Map<String, String> database) {
        database.put(number, print());                    // storage format coupled to the print layout!
    }
}
