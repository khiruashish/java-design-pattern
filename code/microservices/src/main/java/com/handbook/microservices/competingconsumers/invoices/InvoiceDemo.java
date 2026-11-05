package com.handbook.microservices.competingconsumers.invoices;

public class InvoiceDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Grocery: 400 Invoice PDFs Shared by 4 Competing Workers ===");
        InvoiceWorkers workers = new InvoiceWorkers();
        for (int i = 1; i <= 400; i++) {
            workers.publish("ORD-" + i);
        }
        workers.runWith(4);
        long once = workers.processedCount().values().stream().filter(n -> n == 1).count();
        System.out.println("Invoices rendered      : " + workers.processedCount().size());
        System.out.println("Rendered exactly once  : " + once);
        System.out.println("Never more than 4 at a time: " + (workers.maxParallel() <= 4) + " (scale by adding workers, not code)");
    }
}
