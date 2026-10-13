package com.handbook.structural.proxy.banking;

public interface AccountService {

    long balance(String accountNo);

    String withdraw(String accountNo, long amountInr);
}
