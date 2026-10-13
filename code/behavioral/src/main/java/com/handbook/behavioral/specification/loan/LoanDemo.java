package com.handbook.behavioral.specification.loan;

import com.handbook.behavioral.specification.loan.Eligibility.Applicant;

import java.util.List;

public class LoanDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Personal Loan Eligibility ===");
        List<Applicant> applicants = List.of(
                new Applicant("Meena", 32, 85_000, 752, 12_000, true),
                new Applicant("Rohit", 24, 30_000, 690, 14_000, true),
                new Applicant("Sara", 19, 20_000, 720, 0, false));

        for (Applicant a : applicants) {
            var result = Eligibility.check(a, Eligibility.PERSONAL_LOAN);
            System.out.println(a.name() + ": " + (result.eligible() ? "ELIGIBLE" : "NOT ELIGIBLE"));
            result.reasons().forEach(r -> System.out.println("  - " + r));
        }
    }
}
