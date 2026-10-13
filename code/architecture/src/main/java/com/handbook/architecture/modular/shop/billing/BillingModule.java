package com.handbook.architecture.modular.shop.billing;

import com.handbook.architecture.modular.shop.shared.ModuleEvents;
import com.handbook.architecture.modular.shop.shared.OrderPlaced;

import java.util.ArrayList;
import java.util.List;

/** Billing reacts to OrderPlaced. It doesn't depend on the ordering module at all. */
public final class BillingModule {

    private final List<String> invoices = new ArrayList<>();

    public BillingModule(ModuleEvents events) {
        events.on(OrderPlaced.class, this::invoice); // (1)!
    }

    private void invoice(OrderPlaced e) {
        long gst = e.totalPaise() * 5 / 100;
        invoices.add(String.format("INV-%s %s ₹%.2f + GST ₹%.2f", e.orderId(), e.customerId(),
                e.totalPaise() / 100.0, gst / 100.0));
    }

    public List<String> invoices() {
        return List.copyOf(invoices);
    }
}
