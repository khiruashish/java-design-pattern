package com.handbook.principles.other.demeter;

public class Paperboy {
    private long collected;

    /** VIOLATES LoD: reaches through customer into the wallet ("train wreck" a.getB().getC()). */
    public void collectBefore(Customer customer, long due) {
        Wallet wallet = customer.getWallet();
        if (wallet.getCents() >= due) {
            wallet.setCents(wallet.getCents() - due);
            collected += due;
        }
    }

    /** FOLLOWS LoD: only talks to its direct collaborator. */
    public void collectAfter(Customer customer, long due) {
        collected += customer.pay(due);
    }

    public long collected() { return collected; }
}
