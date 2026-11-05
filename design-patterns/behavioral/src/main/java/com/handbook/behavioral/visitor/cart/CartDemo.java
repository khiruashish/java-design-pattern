package com.handbook.behavioral.visitor.cart;

import com.handbook.behavioral.visitor.cart.CartItems.*;

import java.util.List;

public class CartDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Tax and Shipping over a Mixed Cart ===");
        List<Item> cart = List.of(
                new Book("Clean Code", 650, 700),
                new Electronics("Bluetooth Speaker", 2_999, 900, true),
                new FreshProduce("Alphonso Mangoes 1kg", 450, 1_100, true));

        GstVisitor gst = new GstVisitor();
        ShippingVisitor shipping = new ShippingVisitor();
        for (Item item : cart) {
            item.accept(gst);
            item.accept(shipping);
        }
        System.out.printf("GST      : INR %.2f%n", gst.total());
        System.out.println("Shipping : INR " + shipping.costInr() + " (2.7 kg + fragile + chilled)");
    }
}
