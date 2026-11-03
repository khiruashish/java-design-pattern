package com.handbook.enterprise.valueobject.grocery;

import static com.handbook.enterprise.valueobject.grocery.Quantity.Unit.DOZEN;
import static com.handbook.enterprise.valueobject.grocery.Quantity.Unit.GRAM;
import static com.handbook.enterprise.valueobject.grocery.Quantity.Unit.KILOGRAM;
import static com.handbook.enterprise.valueobject.grocery.Quantity.Unit.PIECE;

public class QuantityDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Quantity as a Value Object ===");
        Quantity basket = Quantity.of(500, GRAM).plus(Quantity.of(1, KILOGRAM)).plus(Quantity.of(500, GRAM));
        System.out.println("500 g + 1 kg + 500 g = " + basket);
        System.out.println("1 kg equals 1000 g? " + Quantity.of(1, KILOGRAM).equals(Quantity.of(1000, GRAM)));
        System.out.println("1 dozen equals 12 pc? " + Quantity.of(1, DOZEN).equals(Quantity.of(12, PIECE)));
        System.out.println("Tomatoes 750 g at INR 40/kg = " + Quantity.of(750, GRAM).priceFor(4_000) + " paise");
        try {
            Quantity.of(2, KILOGRAM).plus(Quantity.of(6, PIECE));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        try {
            Quantity.of(-1, PIECE);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
