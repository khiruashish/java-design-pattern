package com.handbook.concurrency.immutable.money;

import java.util.List;
import java.util.stream.IntStream;

public class MoneyDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Splitting a Bill and Summing in Parallel ===");
        Money dinner = Money.inr("100.00");
        List<Money> shares = dinner.allocate(3);
        System.out.println("INR 100 split 3 ways : " + shares);
        System.out.println("Shares add back up   : " + shares.stream().reduce(Money.inr("0"), Money::plus));

        Money oneRupee = Money.inr("1.00"); // shared by all threads, no locks needed
        Money total = IntStream.range(0, 1_000_000).parallel()
                .mapToObj(i -> oneRupee)
                .reduce(Money.inr("0"), Money::plus); // (1)!
        System.out.println("1,000,000 x INR 1 in parallel: " + total);
        System.out.println("Original still INR 1.00      : " + oneRupee);

        try {
            Money.inr("10.005");
        } catch (ArithmeticException e) {
            System.out.println("Rejected INR 10.005: fractions of a paisa are not money");
        }
    }
}
