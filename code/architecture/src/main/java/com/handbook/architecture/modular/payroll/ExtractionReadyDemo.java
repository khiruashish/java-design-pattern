package com.handbook.architecture.modular.payroll;

import com.handbook.architecture.modular.payroll.PayrollModules.HrModule;
import com.handbook.architecture.modular.payroll.PayrollModules.PayrollModule;

import java.util.ArrayList;
import java.util.List;

public class ExtractionReadyDemo {

    public static void main(String[] args) {
        System.out.println("=== HR & Payroll: modules today, services tomorrow ===");

        PayrollModule payroll = new PayrollModule();
        HrModule hr = new HrModule(PayrollModules.inProcess(payroll::on));
        hr.hire("EMP-101", "Meera Nair", 9_500_000);
        hr.hire("EMP-102", "Arjun Das", 7_200_000);
        System.out.println("Modular monolith, in-process events -> payroll has " + payroll.payees());

        List<String> outbox = new ArrayList<>();
        HrModule extractedHr = new HrModule(PayrollModules.outbox(outbox)); // same HrModule class
        extractedHr.hire("EMP-103", "Sara Khan", 8_100_000);
        System.out.println("After extracting payroll, HR writes to the outbox:");
        outbox.forEach(row -> System.out.println("  " + row));
    }
}
