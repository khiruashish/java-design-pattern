package com.handbook.principles.solid.isp;

import com.handbook.principles.solid.isp.after.BasicPrinter;
import com.handbook.principles.solid.isp.after.MultiFunctionDevice;
import com.handbook.principles.solid.isp.after.PrintJob;
import com.handbook.principles.solid.isp.after.Scanner;

import java.util.List;

public class IspDemo {
    public static void main(String[] args) {
        System.out.println("--- Before: one fat interface ---");
        com.handbook.principles.solid.isp.before.OfficeMachine machine = new com.handbook.principles.solid.isp.before.BasicPrinter();
        System.out.println(machine.print("report.pdf"));
        try {
            machine.scan("photo.jpg");       // compiles fine, fails at runtime
        } catch (UnsupportedOperationException e) {
            System.out.println("runtime surprise: " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- After: small role interfaces ---");
        PrintJob job = new PrintJob();
        System.out.println("basic printer: " + job.run(new BasicPrinter(), List.of("a.pdf", "b.pdf")));
        System.out.println("MFD          : " + job.run(new MultiFunctionDevice(), List.of("a.pdf")));
        Scanner scanner = new MultiFunctionDevice();
        System.out.println("scanner role : " + scanner.scan("photo.jpg"));
        System.out.println("(asking a BasicPrinter to scan is now a compile error, not a runtime exception)");
    }
}
