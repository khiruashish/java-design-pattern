package com.handbook.principles.other.demeter;

public class DemeterDemo {
    public static void main(String[] args) {
        Customer ana = new Customer("Ana", new Wallet(1_000));
        Paperboy boy = new Paperboy();
        boy.collectBefore(ana, 300);
        System.out.println("before: paperboy rummaged in the wallet, wallet now " + ana.getWallet().getCents());
        boy.collectAfter(ana, 300);
        System.out.println("after : customer paid, wallet now " + ana.getWallet().getCents() + ", collected " + boy.collected());
        try {
            boy.collectAfter(ana, 5_000);
        } catch (IllegalStateException e) {
            System.out.println("after : the rule lives with the data -> " + e.getMessage());
        }
    }
}
