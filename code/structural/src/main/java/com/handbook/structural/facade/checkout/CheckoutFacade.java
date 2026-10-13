package com.handbook.structural.facade.checkout;

import com.handbook.structural.facade.checkout.Subsystems.DeliveryService;
import com.handbook.structural.facade.checkout.Subsystems.InventoryService;
import com.handbook.structural.facade.checkout.Subsystems.NotificationService;
import com.handbook.structural.facade.checkout.Subsystems.PaymentService;

/** Facade: one call for the app; the workflow and its failure handling live here. */
public class CheckoutFacade {

    public record Result(boolean success, String message) {
    }

    private final InventoryService inventory;
    private final PaymentService payments;
    private final DeliveryService delivery;
    private final NotificationService notifications;

    public CheckoutFacade(InventoryService inventory, PaymentService payments,
                          DeliveryService delivery, NotificationService notifications) { // (1)!
        this.inventory = inventory;
        this.payments = payments;
        this.delivery = delivery;
        this.notifications = notifications;
    }

    public Result placeOrder(String customerId, String address, String sku, int qty, int amountInr) { // (2)!
        if (!inventory.reserve(sku, qty)) {
            return new Result(false, sku + " is out of stock");
        }
        String paymentId;
        try {
            paymentId = payments.charge(customerId, amountInr);
        } catch (IllegalStateException e) {
            inventory.release(sku, qty); // (3)!
            notifications.send(customerId, "Payment failed, nothing was charged");
            return new Result(false, "payment failed: " + e.getMessage());
        }
        String slot = delivery.scheduleSlot(address);
        notifications.send(customerId, "Order confirmed, arriving " + slot);
        return new Result(true, "confirmed " + paymentId + " " + slot);
    }
}
