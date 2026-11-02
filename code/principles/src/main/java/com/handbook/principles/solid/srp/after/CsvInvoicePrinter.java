package com.handbook.principles.solid.srp.after;

import com.handbook.principles.solid.srp.LineItem;

/** Added later for the finance team. Invoice, TaxCalculator and the repository were not touched. */
public class CsvInvoicePrinter implements InvoicePrinter {
    @Override
    public String print(Invoice invoice, long totalCents) {
        StringBuilder sb = new StringBuilder("number,description,quantity,line_total_cents\n");
        for (LineItem i : invoice.items()) {
            sb.append(invoice.number()).append(',').append(i.description()).append(',')
              .append(i.quantity()).append(',').append(i.totalCents()).append('\n');
        }
        return sb.append(invoice.number()).append(",TOTAL,,").append(totalCents).toString();
    }
}
