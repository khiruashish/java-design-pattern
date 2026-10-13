package com.handbook.architecture.layered.grocery.domain;

import java.util.List;

/** Domain layer: plain data and rules about orders. Knows nothing about HTTP or storage. */
public record Order(String id, String customerId, List<Line> lines) {

    public record Line(String sku, int qty, long unitPaise) {
        public long totalPaise() {
            return qty * unitPaise;
        }
    }

    public long totalPaise() {
        return lines.stream().mapToLong(Line::totalPaise).sum();
    }
}
