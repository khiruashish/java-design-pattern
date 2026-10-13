package com.handbook.capstone.shared;

/** Money in paise. Integer arithmetic only: never use double for money. */
public record Money(long paise) implements Comparable<Money> {

    public static final Money ZERO = new Money(0);

    public static Money rupees(long rupees) {
        return new Money(rupees * 100);
    }

    public Money plus(Money other) {
        return new Money(paise + other.paise);
    }

    public Money minus(Money other) {
        return new Money(Math.max(0, paise - other.paise));
    }

    public Money times(int qty) {
        return new Money(paise * qty);
    }

    public Money percent(int pct) {
        return new Money(paise * pct / 100);
    }

    @Override
    public int compareTo(Money o) {
        return Long.compare(paise, o.paise);
    }

    @Override
    public String toString() {
        return String.format("₹%,d.%02d", paise / 100, paise % 100);
    }
}
