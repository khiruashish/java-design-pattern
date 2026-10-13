package com.handbook.behavioral.specification.loan;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Explainable specifications: every failed rule reports WHY (regulators require reasons). */
public final class Eligibility {

    private Eligibility() {
    }

    public record Applicant(String name, int age, long monthlyIncomeInr, int creditScore, long existingEmiInr, boolean kycDone) {
    }

    public record Rule(String reason, Predicate<Applicant> test) {
    }

    public record Result(boolean eligible, List<String> reasons) {
    }

    public static final List<Rule> PERSONAL_LOAN = List.of(
            new Rule("age must be 21-60", a -> a.age() >= 21 && a.age() <= 60),
            new Rule("KYC must be complete", Applicant::kycDone),
            new Rule("credit score must be at least 700", a -> a.creditScore() >= 700),
            new Rule("monthly income must be at least INR 25,000", a -> a.monthlyIncomeInr() >= 25_000),
            new Rule("existing EMIs must be under 40% of income", a -> a.existingEmiInr() * 100 < a.monthlyIncomeInr() * 40));

    public static Result check(Applicant a, List<Rule> rules) {
        List<String> failed = new ArrayList<>();
        for (Rule r : rules) {
            if (!r.test().test(a)) {
                failed.add(r.reason()); // (1)!
            }
        }
        return new Result(failed.isEmpty(), failed);
    }
}
