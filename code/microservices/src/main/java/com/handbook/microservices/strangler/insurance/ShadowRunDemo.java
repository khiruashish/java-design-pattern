package com.handbook.microservices.strangler.insurance;

import java.util.List;

public class ShadowRunDemo {

    record Driver(String name, int age, int claims) {
    }

    public static void main(String[] args) {
        // Legacy COBOL-era rule: base 12,000; under 25 +40%; each past claim +15% (compounded).
        var shadow = new ShadowComparator<Driver>(d -> {
            double premium = 12_000 * (d.age() < 25 ? 1.4 : 1.0);
            for (int i = 0; i < d.claims(); i++) {
                premium *= 1.15;
            }
            return Math.round(premium);
        }, d -> {
            // Rewritten rule: someone read the spec as "+15% per claim" (simple, not compounded).
            double premium = 12_000 * (d.age() < 25 ? 1.4 : 1.0) * (1 + 0.15 * d.claims());
            return Math.round(premium);
        });

        System.out.println("=== Insurance: parallel run of the new premium engine ===");
        for (Driver d : List.of(new Driver("Asha", 34, 0), new Driver("Ravi", 22, 0),
                new Driver("Meera", 41, 1), new Driver("Kabir", 29, 2), new Driver("Zoya", 23, 3))) {
            System.out.println(d.name() + " quoted " + shadow.quote(d) + " (legacy answer returned)");
        }
        System.out.println("Compared " + shadow.compared() + ", mismatches " + shadow.mismatches().size() + ":");
        shadow.mismatches().forEach(m -> System.out.println("  " + m));
        System.out.println("Not ready to switch: compounding rule differs for 2+ claims.");
    }
}
