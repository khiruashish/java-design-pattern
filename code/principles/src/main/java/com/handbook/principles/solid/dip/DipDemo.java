package com.handbook.principles.solid.dip;

import com.handbook.principles.solid.dip.after.EmailSender;
import com.handbook.principles.solid.dip.after.MessageSender;
import com.handbook.principles.solid.dip.after.OrderNotifier;
import com.handbook.principles.solid.dip.after.SmsSender;

import java.util.ArrayList;
import java.util.List;

public class DipDemo {
    public static void main(String[] args) {
        System.out.println("--- Before: notifier news up EmailSender itself ---");
        System.out.println(new com.handbook.principles.solid.dip.before.OrderNotifier().orderShipped("ana@example.com", "A-100"));

        System.out.println();
        System.out.println("--- After: the sender is injected ---");
        System.out.println(new OrderNotifier(new EmailSender()).orderShipped("ana@example.com", "A-100"));
        System.out.println(new OrderNotifier(new SmsSender()).orderShipped("+1-555-0100", "A-100"));

        List<String> outbox = new ArrayList<>();
        MessageSender fake = (to, text) -> { outbox.add(to + "|" + text); return "recorded"; };   // test double
        new OrderNotifier(fake).orderShipped("test-user", "A-100");
        System.out.println("fake sender captured: " + outbox);
    }
}
