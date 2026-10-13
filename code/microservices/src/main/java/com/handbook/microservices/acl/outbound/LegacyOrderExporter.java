package com.handbook.microservices.acl.outbound;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Coding challenge: the ACL works in both directions. The warehouse system only accepts orders as
 * fixed-width records: 10-char order number, 20-char customer name (upper case, no accents),
 * quantity right-aligned in 5 digits, and a YYMMDD date. Translate our Order into that format.
 */
public final class LegacyOrderExporter {

    public record OrderLine(String orderId, String customerName, int quantity, LocalDate date) {
    }

    public static String toRecord(OrderLine o) {
        String id = o.orderId().replace("-", "");
        String name = Normalizer.normalize(o.customerName(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase();
        if (o.quantity() < 0 || o.quantity() > 99_999) {
            throw new IllegalArgumentException("quantity does not fit in 5 digits: " + o.quantity());
        }
        return pad(id, 10) + pad(name, 20) + String.format("%05d", o.quantity())
                + o.date().format(DateTimeFormatter.ofPattern("yyMMdd"));
    }

    private static String pad(String s, int width) {
        return s.length() >= width ? s.substring(0, width) : s + " ".repeat(width - s.length());
    }

    public static List<String> export(List<OrderLine> orders) {
        return orders.stream().map(LegacyOrderExporter::toRecord).toList();
    }
}
