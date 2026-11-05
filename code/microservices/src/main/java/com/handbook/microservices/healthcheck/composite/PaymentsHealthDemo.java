package com.handbook.microservices.healthcheck.composite;

import com.handbook.microservices.healthcheck.composite.CompositeHealth.Component;
import com.handbook.microservices.healthcheck.composite.CompositeHealth.Report;

import java.util.List;

public class PaymentsHealthDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: One Health Endpoint, Components with Different Criticality ===");
        print("Normal day", List.of(new Component("ledgerDb", true, true), new Component("primaryPsp", true, true),
                new Component("fraudScoring", false, true), new Component("emailReceipts", false, true)));
        print("Fraud scorer down", List.of(new Component("ledgerDb", true, true), new Component("primaryPsp", true, true),
                new Component("fraudScoring", false, false), new Component("emailReceipts", false, true)));
        print("Ledger DB down", List.of(new Component("ledgerDb", true, false), new Component("primaryPsp", true, true),
                new Component("fraudScoring", false, true), new Component("emailReceipts", false, false)));
        System.out.println("DEGRADED stays ready (fallback: rule-based fraud check) but pages the on-call; DOWN stops traffic.");
    }

    private static void print(String scenario, List<Component> components) {
        Report r = CompositeHealth.evaluate(components);
        System.out.printf("%-18s -> %-8s %s%n", scenario, r.status(), r.details());
    }
}
