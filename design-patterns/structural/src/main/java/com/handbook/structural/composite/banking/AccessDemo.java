package com.handbook.structural.composite.banking;

import java.util.Set;
import java.util.TreeSet;

public class AccessDemo {

    public static void main(String[] args) {
        System.out.println("=== Core Banking: Nested Roles ===");
        Role teller = new Role("Teller")
                .include(new Permission("account.view"))
                .include(new Permission("cash.deposit"))
                .include(new Permission("cash.withdraw.small"));
        Role loanOfficer = new Role("LoanOfficer")
                .include(new Permission("account.view"))
                .include(new Permission("loan.create"));
        Role branchManager = new Role("BranchManager")
                .include(teller)
                .include(loanOfficer)
                .include(new Permission("cash.withdraw.large"))
                .include(new Permission("loan.approve"));

        for (Role role : new Role[] {teller, branchManager}) {
            Set<String> all = new TreeSet<>();
            role.collect(all);
            System.out.println(role.name() + " -> " + all);
        }
        System.out.println("Teller can approve loans? " + teller.grants("loan.approve"));
        System.out.println("BranchManager can deposit cash (via Teller)? " + branchManager.grants("cash.deposit"));

        try {
            teller.include(branchManager);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
