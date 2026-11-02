package com.handbook.principles.solid.dip.after;

/** High-level policy depends only on MessageSender; the concrete sender is injected through the constructor. */
public class OrderNotifier {
    private final MessageSender sender;

    public OrderNotifier(MessageSender sender) {
        this.sender = sender;
    }

    public String orderShipped(String customer, String orderId) {
        return sender.send(customer, "Order " + orderId + " has shipped");
    }
}
