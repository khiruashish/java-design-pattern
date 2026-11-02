package com.handbook.principles.solid.srp;

import com.handbook.principles.solid.srp.after.CsvInvoicePrinter;
import com.handbook.principles.solid.srp.after.InvoicePrinter;
import com.handbook.principles.solid.srp.after.InvoiceRepository;
import com.handbook.principles.solid.srp.after.TaxCalculator;
import com.handbook.principles.solid.srp.after.TextInvoicePrinter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SrpDemo {
    public static void main(String[] args) {
        List<LineItem> items = List.of(new LineItem("Keyboard", 1, 4_500), new LineItem("Mouse", 2, 1_250));

        System.out.println("--- Before: one class does everything ---");
        var before = new com.handbook.principles.solid.srp.before.Invoice("INV-1", items);
        Map<String, String> db = new HashMap<>();
        before.save(db);
        System.out.println(before.print());
        System.out.println("stored value is the printed text: " + db.get("INV-1").startsWith("INVOICE"));

        System.out.println();
        System.out.println("--- After: one reason to change per class ---");
        var invoice = new com.handbook.principles.solid.srp.after.Invoice("INV-1", items);
        var tax = new TaxCalculator(18);
        var repository = new InvoiceRepository();
        repository.save(invoice);
        long total = tax.totalWithTaxCents(invoice);
        for (InvoicePrinter printer : List.of(new TextInvoicePrinter(), new CsvInvoicePrinter())) {
            System.out.println(printer.print(invoice, total));
            System.out.println();
        }
        System.out.println("tax rule changed to 5%: " + new TaxCalculator(5).totalWithTaxCents(invoice)
                + " cents (no printer or repository edited)");
    }
}
