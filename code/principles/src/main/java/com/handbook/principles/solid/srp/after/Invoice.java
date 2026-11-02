package com.handbook.principles.solid.srp.after;

import com.handbook.principles.solid.srp.LineItem;

import java.util.List;

/** Data and the rules that belong to the invoice itself. Its only reason to change: what an invoice IS. */
public record Invoice(String number, List<LineItem> items) {
    public Invoice {
        items = List.copyOf(items);
    }

    public long subtotalCents() {
        return items.stream().mapToLong(LineItem::totalCents).sum();
    }
}
