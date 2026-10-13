package com.handbook.microservices.acl.outbound;

import com.handbook.microservices.acl.outbound.LegacyOrderExporter.OrderLine;

import java.time.LocalDate;
import java.util.List;

public class LegacyExportDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: outbound translation to a fixed-width legacy format ===");
        List<String> records = LegacyOrderExporter.export(List.of(
                new OrderLine("ORD-1", "Asha Rao", 2, LocalDate.of(2026, 10, 13)),
                new OrderLine("ORD-12345678901", "Zoë Fernandes-D'Souza", 12, LocalDate.of(2026, 10, 13))));
        System.out.println("|" + "0123456789".repeat(5).substring(0, 41) + "|");
        records.forEach(r -> System.out.println("|" + r + "|"));
        System.out.println("Every record is 41 characters: " + records.stream().allMatch(r -> r.length() == 41));
    }
}
