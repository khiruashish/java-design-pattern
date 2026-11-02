package com.handbook.principles.solid.srp.after;

/** Reason to change: how an invoice looks. A new format means a new printer, not edits to Invoice. */
public interface InvoicePrinter {
    String print(Invoice invoice, long totalCents);
}
