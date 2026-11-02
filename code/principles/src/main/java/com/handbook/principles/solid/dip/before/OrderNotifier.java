package com.handbook.principles.solid.dip.before;

/**
 * VIOLATES DIP: the high-level policy ("tell the customer their order shipped") creates and depends on a
 * concrete low-level class. Switching to SMS, or testing without sending real email, means editing this class.
 */
public class OrderNotifier {
    private final EmailSender sender = new EmailSender();   // hard-wired dependency

    public String orderShipped(String customer, String orderId) {
        return sender.send(customer, "Order " + orderId + " has shipped");
    }
}
