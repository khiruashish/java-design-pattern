package com.handbook.structural.proxy.banking;

import java.util.HashMap;
import java.util.Map;

/** Real subject: pure business logic, no security code. */
public class CoreAccountService implements AccountService {

    private final Map<String, Long> balances = new HashMap<>(Map.of("SB-1001", 250_000L));

    public long balance(String accountNo) {
        return balances.get(accountNo);
    }

    public String withdraw(String accountNo, long amount) {
        balances.merge(accountNo, -amount, Long::sum);
        return "withdrew INR " + amount + ", new balance INR " + balances.get(accountNo);
    }
}
