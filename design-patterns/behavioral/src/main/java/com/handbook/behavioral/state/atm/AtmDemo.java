package com.handbook.behavioral.state.atm;

public class AtmDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: ATM Session ===");
        Atm atm = new Atm();
        System.out.println("Customer 1:");
        atm.withdraw(500);
        atm.insertCard("XXXX-1111");
        atm.enterPin("0000");
        atm.enterPin("4321");
        atm.withdraw(2_550);
        atm.withdraw(2_500);
        atm.eject();

        System.out.println("Customer 2 (forgot PIN):");
        atm.insertCard("XXXX-2222");
        atm.enterPin("1111");
        atm.enterPin("2222");
        atm.enterPin("3333");
        atm.withdraw(1_000);
    }
}
