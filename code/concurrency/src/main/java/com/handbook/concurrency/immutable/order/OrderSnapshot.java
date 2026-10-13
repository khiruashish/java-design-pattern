package com.handbook.concurrency.immutable.order;

import java.time.Instant;
import java.util.List;

/** What the customer agreed to at checkout. Shared with billing, warehouse and email threads. */
public record OrderSnapshot(String orderId, Instant placedAt, List<Line> lines) {

    public record Line(String sku, int qty, int unitPriceInr) {
    }

    public OrderSnapshot {
        lines = List.copyOf(lines); // (1)!
    }

    public int totalInr() {
        return lines.stream().mapToInt(l -> l.qty() * l.unitPriceInr()).sum();
    }

    public OrderSnapshot withLine(Line extra) { // (2)!
        var copy = new java.util.ArrayList<>(lines);
        copy.add(extra);
        return new OrderSnapshot(orderId, placedAt, copy);
    }
}
