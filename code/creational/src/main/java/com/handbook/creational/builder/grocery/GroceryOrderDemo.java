package com.handbook.creational.builder.grocery;

import com.handbook.creational.builder.grocery.GroceryOrder.Slot;

public class GroceryOrderDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Building Orders ===");

        GroceryOrder quick = GroceryOrder.builder("CUST-42", "12 MG Road, Bengaluru")
                .item("MILK-1L", 2, 68)
                .item("BREAD-WW", 1, 55)
                .slot(Slot.EXPRESS_10_MIN)
                .build();
        System.out.println(quick);

        GroceryOrder weekly = GroceryOrder.builder("CUST-7", "4 Park Street, Kolkata")
                .item("RICE-5KG", 1, 420)
                .item("DAL-1KG", 2, 140)
                .item("OIL-1L", 2, 180)
                .coupon("WEEKLY50")
                .instructions("Leave with security")
                .contactless()
                .build();
        System.out.println(weekly);

        try {
            GroceryOrder.builder("CUST-9", "1 Marine Drive, Mumbai")
                    .item("TV-55IN", 1, 45_000)
                    .slot(Slot.EXPRESS_10_MIN)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        try {
            GroceryOrder.builder("CUST-9", "1 Marine Drive, Mumbai").build();
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
