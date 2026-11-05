package com.handbook.microservices.deployment.migration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Coding challenge: rename customers.mobile -> customers.phone_e164 without breaking old or new app versions. */
public class ExpandContractDemo {

    /** Columns that exist at each step, and which app versions are running during it. */
    record Step(String description, Set<String> columns, List<String> appsRunning) { }

    static boolean appWorks(String app, Set<String> columns) {
        return switch (app) {
            case "v1" -> columns.contains("mobile");                                   // reads/writes mobile
            case "v2" -> columns.contains("mobile") && columns.contains("phone_e164"); // writes both, reads new
            case "v3" -> columns.contains("phone_e164");                               // only the new column
            default -> throw new IllegalArgumentException(app);
        };
    }

    static void check(String title, List<Step> steps) {
        System.out.println(title);
        for (Step s : steps) {
            Map<String, Boolean> ok = new LinkedHashMap<>();
            s.appsRunning().forEach(a -> ok.put(a, appWorks(a, s.columns())));
            boolean safe = !ok.containsValue(false);
            System.out.printf("  %-45s apps %s -> %s%n", s.description(), ok, safe ? "ok" : "OUTAGE");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: Zero-Downtime Column Rename (rolling deploys run old + new together) ===");
        check("Big-bang rename:", List.of(
                new Step("ALTER TABLE ... RENAME mobile TO phone_e164", Set.of("phone_e164"), List.of("v1", "v3"))));
        check("Expand / migrate / contract:", List.of(
                new Step("1. expand: ADD COLUMN phone_e164", Set.of("mobile", "phone_e164"), List.of("v1")),
                new Step("2. deploy v2 (dual-write, read new) + backfill", Set.of("mobile", "phone_e164"), List.of("v1", "v2")),
                new Step("3. deploy v3 (new column only)", Set.of("mobile", "phone_e164"), List.of("v2", "v3")),
                new Step("4. contract: DROP COLUMN mobile", Set.of("phone_e164"), List.of("v3"))));
    }
}
