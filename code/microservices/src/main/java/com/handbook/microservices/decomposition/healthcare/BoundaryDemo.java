package com.handbook.microservices.decomposition.healthcare;

import com.handbook.microservices.decomposition.healthcare.BoundaryChecker.UseCase;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoundaryDemo {

    static final List<UseCase> USE_CASES = List.of(
            new UseCase("Book appointment", Set.of("appointments", "slots"), Set.of("patients", "doctors")),
            new UseCase("Check in patient", Set.of("appointments"), Set.of("patients")),
            new UseCase("Close consultation + bill", Set.of("appointments", "invoices"), Set.of("tariffs")),
            new UseCase("Pay invoice", Set.of("invoices", "payments"), Set.of()),
            new UseCase("Update allergies", Set.of("patients"), Set.of()));

    public static void main(String[] args) {
        System.out.println("=== Hospital: Checking Two Candidate Service Splits ===");
        Map<String, String> splitByTeam = Map.of( // split along today's org chart
                "patients", "Registration", "appointments", "Scheduling", "slots", "Doctors",
                "doctors", "Doctors", "invoices", "Billing", "payments", "Payments", "tariffs", "Billing");
        Map<String, String> splitByCapability = Map.of( // split along business capabilities
                "patients", "PatientRecords", "appointments", "Scheduling", "slots", "Scheduling",
                "doctors", "Scheduling", "invoices", "Billing", "payments", "Billing", "tariffs", "Billing");

        for (var split : List.of(Map.entry("Split A (by current teams)", splitByTeam),
                Map.entry("Split B (by business capability)", splitByCapability))) {
            System.out.println(split.getKey() + ":");
            BoundaryChecker.check(split.getValue(), USE_CASES)
                    .forEach((uc, verdict) -> System.out.printf("  %-26s %s%n", uc, verdict));
        }
    }
}
