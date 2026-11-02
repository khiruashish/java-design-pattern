package com.handbook.principles.oop.polymorphism;

/** Sealed hierarchy: the compiler knows every subtype, enabling exhaustive switch (Java 21). */
public sealed interface Payment permits Payment.Card, Payment.Upi, Payment.Cash {

    long amountCents();

    /** Runtime (subtype) polymorphism: each subtype overrides fee(). */
    long feeCents();

    record Card(long amountCents, String last4) implements Payment {
        public long feeCents() { return amountCents * 2 / 100; }       // 2%
    }

    record Upi(long amountCents, String vpa) implements Payment {
        public long feeCents() { return 0; }
    }

    record Cash(long amountCents) implements Payment {
        public long feeCents() { return 0; }
    }
}
