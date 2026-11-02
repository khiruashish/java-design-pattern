package com.handbook.principles.oop.polymorphism;

/** Compile-time (ad-hoc) polymorphism: overloading picks a method by the static argument types. */
public final class Receipt {
    private Receipt() { }

    public static String line(String label, long cents) { return label + ": " + cents + " cents"; }

    public static String line(String label, double percent) { return label + ": " + percent + "%"; }

    /** Pattern-matching switch over a sealed type: no default branch needed, adding a subtype is a compile error here. */
    public static String channel(Payment p) {
        return switch (p) {
            case Payment.Card c -> "card ending " + c.last4();
            case Payment.Upi u -> "UPI " + u.vpa();
            case Payment.Cash cash -> "cash";
        };
    }
}
