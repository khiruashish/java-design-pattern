package com.handbook.behavioral.template.payment;

import com.handbook.behavioral.template.payment.Flows.CardFlow;
import com.handbook.behavioral.template.payment.Flows.UpiFlow;

public class PaymentDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Same Steps, Different Methods ===");
        System.out.println("Card, INR 1,200:");
        new CardFlow().process("ORD-1", 1_200);
        System.out.println("Card, INR 18,000:");
        new CardFlow().process("ORD-2", 18_000);
        System.out.println("UPI, INR 18,000:");
        new UpiFlow().process("ORD-3", 18_000);
    }
}
