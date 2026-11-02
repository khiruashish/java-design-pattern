package com.handbook.principles.other.demeter;

public class Wallet {
    private long cents;

    public Wallet(long cents) { this.cents = cents; }

    public long getCents() { return cents; }

    public void setCents(long cents) { this.cents = cents; }
}
