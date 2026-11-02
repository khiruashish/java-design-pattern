package com.handbook.principles.other.demeter;

/** Exposes its wallet (Before) AND offers a behaviour that hides it (After). */
public class Customer {
    private final String name;
    private final Wallet wallet;

    public Customer(String name, Wallet wallet) {
        this.name = name;
        this.wallet = wallet;
    }

    public String name() { return name; }

    /** Before-style accessor: leaks the internal structure. */
    public Wallet getWallet() { return wallet; }

    /** After: the customer decides how to pay; callers only talk to their direct friend. */
    public long pay(long cents) {
        if (wallet.getCents() < cents) throw new IllegalStateException(name + " cannot pay " + cents);
        wallet.setCents(wallet.getCents() - cents);
        return cents;
    }
}
