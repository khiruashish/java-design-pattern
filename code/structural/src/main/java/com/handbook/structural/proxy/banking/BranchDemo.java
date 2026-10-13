package com.handbook.structural.proxy.banking;

import com.handbook.structural.proxy.banking.SecuredAccountService.User;

import java.util.Set;

public class BranchDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Branch Counter ===");
        AccountService core = new CoreAccountService();
        AccountService asTeller = new SecuredAccountService(core, new User("anita", Set.of("TELLER")));
        AccountService asManager = new SecuredAccountService(core, new User("vikram", Set.of("MANAGER")));
        AccountService asIntern = new SecuredAccountService(core, new User("sam", Set.of("INTERN")));

        attempt("Teller checks balance  ", () -> "INR " + asTeller.balance("SB-1001"));
        attempt("Teller withdraws 20,000", () -> asTeller.withdraw("SB-1001", 20_000));
        attempt("Teller withdraws 80,000", () -> asTeller.withdraw("SB-1001", 80_000));
        attempt("Manager withdraws 80,000", () -> asManager.withdraw("SB-1001", 80_000));
        attempt("Intern checks balance  ", () -> "INR " + asIntern.balance("SB-1001"));
    }

    private static void attempt(String label, java.util.function.Supplier<String> action) {
        try {
            System.out.println(label + " -> " + action.get());
        } catch (SecurityException e) {
            System.out.println(label + " -> DENIED (" + e.getMessage() + ")");
        }
    }
}
