package com.handbook.enterprise.aggregate.grocery;

public class OrderAggregateDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: The Order Aggregate Guards Its Own Rules ===");
        Order order = new Order("ORD-9", "CUST-7");
        order.add("ALPHONSO-1DZ", 89_900, 2);
        order.add("ALPHONSO-1DZ", 89_900, 2);
        order.add("MILK-1L", 6_800, 3);
        System.out.println("Lines " + order.lines() + ", total INR " + order.total() / 100);
        attempt("5th dozen of mangoes", () -> order.add("ALPHONSO-1DZ", 89_900, 1));
        attempt("INR 18,000 TV in a grocery order", () -> order.add("TV-32", 1_800_000, 1));
        attempt("edit lines from outside", () -> order.lines().clear());
        order.markPacked();
        attempt("add after packing", () -> order.add("BREAD", 4_500, 1));
        System.out.println("Final: " + order.status() + " " + order.lines() + ", total INR " + order.total() / 100);
    }

    private static void attempt(String what, Runnable r) {
        try {
            r.run();
            System.out.println("  " + what + ": allowed?!");
        } catch (RuntimeException e) {
            System.out.println("  " + what + ": rejected (" + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()) + ")");
        }
    }
}
