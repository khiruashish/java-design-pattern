package com.handbook.concurrency.activeobject.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class PrinterDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: Shipping-Label Printer with a Bounded Queue ===");
        CountDownLatch paper = new CountDownLatch(1); // printer is warming up
        try (LabelPrinter printer = new LabelPrinter(3, paper)) {
            List<CompletableFuture<Integer>> jobs = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                jobs.add(printer.print("AWB-" + i));
            }
            paper.countDown();
            for (int i = 0; i < jobs.size(); i++) {
                CompletableFuture<Integer> job = jobs.get(i);
                String outcome = job.handle((n, err) -> err == null ? "printed as label #" + n : "REJECTED: " + err.getMessage()).join();
                System.out.println("AWB-" + (i + 1) + ": " + outcome);
            }
            System.out.println("Print order: " + printer.printedSoFar().join());
        }
    }
}
