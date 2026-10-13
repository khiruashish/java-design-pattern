package com.handbook.structural.proxy.banking;

import java.util.Set;

/** Protection proxy: checks the caller's roles before forwarding to the real service. */
public class SecuredAccountService implements AccountService {

    public record User(String name, Set<String> roles) {
    }

    private static final long TELLER_LIMIT_INR = 50_000;

    private final AccountService target;
    private final User user;

    public SecuredAccountService(AccountService target, User user) {
        this.target = target;
        this.user = user;
    }

    @Override
    public long balance(String accountNo) {
        require("TELLER", "MANAGER");
        return target.balance(accountNo);
    }

    @Override
    public String withdraw(String accountNo, long amountInr) {
        if (amountInr > TELLER_LIMIT_INR) {
            require("MANAGER"); // (1)!
        } else {
            require("TELLER", "MANAGER");
        }
        return target.withdraw(accountNo, amountInr);
    }

    private void require(String... anyOf) {
        for (String role : anyOf) {
            if (user.roles().contains(role)) {
                return;
            }
        }
        throw new SecurityException(user.name() + " lacks role " + String.join("/", anyOf)); // (2)!
    }
}
