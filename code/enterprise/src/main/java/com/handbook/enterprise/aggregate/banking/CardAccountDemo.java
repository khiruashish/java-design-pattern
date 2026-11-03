package com.handbook.enterprise.aggregate.banking;

public class CardAccountDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Credit Card Account as One Consistency Boundary ===");
        CardAccount card = new CardAccount("CARD-4111", 5_000_000); // INR 50,000 limit
        System.out.println("Hotel pre-auth 30,000 : " + card.authorise("A1", 3_000_000) + " -> " + card);
        System.out.println("Fuel pre-auth 2,000   : " + card.authorise("A2", 200_000) + " -> " + card);
        System.out.println("Laptop 25,000         : " + card.authorise("A3", 2_500_000) + " -> " + card);
        card.capture("A1", 2_400_000);   // final hotel bill lower than the hold
        System.out.println("Hotel captured 24,000 -> " + card);
        card.release("A2");              // fuel station sent a reversal
        System.out.println("Fuel released         -> " + card);
        System.out.println("Laptop retried        : " + card.authorise("A3", 2_500_000) + " -> " + card);
        System.out.println("Version after 5 state changes: " + card.version() + " (used for optimistic locking on save)");
    }
}
