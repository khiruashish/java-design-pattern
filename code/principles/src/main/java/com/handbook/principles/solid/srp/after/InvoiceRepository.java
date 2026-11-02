package com.handbook.principles.solid.srp.after;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Reason to change: how invoices are stored. Stores the data, not a printed layout. */
public class InvoiceRepository {
    private final Map<String, Invoice> store = new HashMap<>();

    public void save(Invoice invoice) {
        store.put(invoice.number(), invoice);
    }

    public Optional<Invoice> find(String number) {
        return Optional.ofNullable(store.get(number));
    }
}
