package com.handbook.creational.prototype.cart;

public class ReorderDemo {

    public static void main(String[] args) {
        Cart lastWeek = new Cart("CUST-42").add("MILK-1L", 2).add("EGGS-12", 1);
        Cart reorder = lastWeek.copy();
        reorder.line(0).setQuantity(4);
        reorder.add("BREAD-WW", 1);

        System.out.println("Last week: " + lastWeek);
        System.out.println("Reorder:   " + reorder);
    }
}
