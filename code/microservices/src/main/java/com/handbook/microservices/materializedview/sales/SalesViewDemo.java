package com.handbook.microservices.materializedview.sales;

public class SalesViewDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Store Sales Dashboard on a Refreshed Summary Table ===");
        StoreSalesView view = new StoreSalesView();
        view.insertLines("BLR-HSR", "2026-07-01", 12_000, 41_500);
        view.insertLines("BLR-KORA", "2026-07-01", 9_000, 38_000);
        view.refresh(); // nightly / every 5 minutes
        System.out.println("Raw order lines: " + view.rawLines() + "; dashboard reads 1 precomputed row:");
        System.out.println("  BLR-HSR  2026-07-01 -> " + view.dashboard("BLR-HSR", "2026-07-01"));

        view.insertLines("BLR-HSR", "2026-07-01", 500, 41_500); // evening orders after the last refresh
        System.out.println("Before next refresh -> " + view.dashboard("BLR-HSR", "2026-07-01") + " (stale by design)");
        view.refresh();
        System.out.println("After refresh       -> " + view.dashboard("BLR-HSR", "2026-07-01"));
    }
}
