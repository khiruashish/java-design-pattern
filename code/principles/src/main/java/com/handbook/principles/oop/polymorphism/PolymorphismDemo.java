package com.handbook.principles.oop.polymorphism;

import java.util.List;

public class PolymorphismDemo {
    public static void main(String[] args) {
        List<Payment> payments = List.of(new Payment.Card(10_000, "4242"), new Payment.Upi(5_000, "ana@bank"), new Payment.Cash(700));
        for (Payment p : payments) {
            System.out.println(Receipt.channel(p) + " | " + Receipt.line("amount", p.amountCents()) + " | " + Receipt.line("fee", p.feeCents()));
        }
        System.out.println(Receipt.line("card fee rate", 2.0));
    }
}
