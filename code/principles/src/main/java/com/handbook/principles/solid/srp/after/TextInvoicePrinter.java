package com.handbook.principles.solid.srp.after;

import com.handbook.principles.solid.srp.LineItem;

public class TextInvoicePrinter implements InvoicePrinter {
    @Override
    public String print(Invoice invoice, long totalCents) {
        StringBuilder sb = new StringBuilder("INVOICE " + invoice.number() + "\n");
        for (LineItem i : invoice.items()) {
            sb.append(String.format("  %-10s x%d  %8.2f%n", i.description(), i.quantity(), i.totalCents() / 100.0));
        }
        return sb.append(String.format("  TOTAL (incl. tax) %8.2f", totalCents / 100.0)).toString();
    }
}
