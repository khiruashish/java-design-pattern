package com.handbook.lld.splitwise;

import java.util.List;
import java.util.Map;

public class ExpenseSharingDemo {

    public static void main(String[] args) {
        var trip = new ExpenseSharing();
        List<String> all = List.of("Asha", "Ravi", "Meera", "Kabir");

        System.out.println("=== LLD: Splitwise-style expense sharing (Goa trip) ===");
        trip.addExpense("Asha", 1_200_000, all, ExpenseSharing.equal());                    // villa ₹12,000
        trip.addExpense("Ravi", 300_001, all, ExpenseSharing.equal());                      // dinner ₹3,000.01
        trip.addExpense("Meera", 400_000, List.of("Meera", "Kabir"),
                ExpenseSharing.percent(Map.of("Meera", 25, "Kabir", 75)));                  // scooter ₹4,000
        trip.addExpense("Kabir", 150_000, List.of("Asha", "Kabir"),
                ExpenseSharing.exact(Map.of("Asha", 100_000L, "Kabir", 50_000L)));          // shopping ₹1,500

        trip.balances().forEach((who, b) -> System.out.printf("%-6s %s ₹%,.2f%n", who, b >= 0 ? "gets back" : "owes     ",
                Math.abs(b) / 100.0));
        System.out.println("Sum of balances: " + trip.balances().values().stream().mapToLong(Long::longValue).sum() + " paise");
        System.out.println("Settle up with the fewest payments:");
        trip.settleUp().forEach(p -> System.out.println("  " + p));
    }
}
