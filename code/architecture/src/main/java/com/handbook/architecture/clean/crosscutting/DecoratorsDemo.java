package com.handbook.architecture.clean.crosscutting;

import com.handbook.architecture.clean.crosscutting.UseCaseDecorators.TxManager;
import com.handbook.architecture.clean.crosscutting.UseCaseDecorators.UseCase;

public class DecoratorsDemo {

    public static void main(String[] args) {
        // The interactor: pure business logic, no transactions, no logging, no security.
        UseCase<Long, String> issueRefund = paise -> {
            if (paise > 500_000) {
                throw new IllegalArgumentException("refunds over ₹5,000 need a manager");
            }
            return "refund of ₹" + paise / 100 + " issued";
        };

        TxManager tx = new TxManager();
        String[] role = {"SUPPORT_AGENT"};
        UseCase<Long, String> wired = UseCaseDecorators.logged("IssueRefund",
                UseCaseDecorators.requiresRole("SUPPORT_AGENT", () -> role[0],
                        UseCaseDecorators.transactional(issueRefund, tx)));

        System.out.println("=== Challenge: cross-cutting concerns as decorators at the edge ===");
        wired.execute(29_900L);
        try {
            wired.execute(900_000L);
        } catch (IllegalArgumentException e) {
            System.out.println("    failed: " + e.getMessage());
        }
        role[0] = "WAREHOUSE";
        try {
            wired.execute(10_000L);
        } catch (SecurityException e) {
            System.out.println("    denied: " + e.getMessage());
        }
        System.out.println("transaction log: " + tx.log);
    }
}
