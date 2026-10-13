package com.handbook.concurrency.immutable.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

/** Immutable value object: every operation returns a NEW Money. Safe to share across threads. */
public record Money(BigDecimal amount, Currency currency) {

    public Money { // (1)!
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
    }

    public static Money inr(String amount) {
        return new Money(new BigDecimal(amount), Currency.getInstance("INR"));
    }

    public Money plus(Money other) { // (2)!
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    /** Split into n parts that add back up EXACTLY: the extra paise go to the first parts. */
    public List<Money> allocate(int parts) {
        BigDecimal unit = BigDecimal.ONE.movePointLeft(currency.getDefaultFractionDigits()); // 0.01
        BigDecimal base = amount.divide(BigDecimal.valueOf(parts), currency.getDefaultFractionDigits(), RoundingMode.DOWN);
        BigDecimal remainder = amount.subtract(base.multiply(BigDecimal.valueOf(parts)));
        int extra = remainder.divide(unit, RoundingMode.UNNECESSARY).intValue();
        List<Money> result = new ArrayList<>();
        for (int i = 0; i < parts; i++) {
            result.add(new Money(i < extra ? base.add(unit) : base, currency));
        }
        return List.copyOf(result);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add " + other.currency + " to " + currency);
        }
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount.toPlainString();
    }
}
