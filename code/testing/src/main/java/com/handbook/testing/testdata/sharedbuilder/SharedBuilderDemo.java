package com.handbook.testing.testdata.sharedbuilder;

/** Coding challenge: a shared mutable builder leaks changes between tests. Fix: fresh or immutable builders. */
public class SharedBuilderDemo {

    record Account(String type, long balancePaise, boolean frozen) { }

    static final class MutableBuilder {
        String type = "SAVINGS";
        long balance = 100_000;
        boolean frozen = false;
        MutableBuilder frozen() { frozen = true; return this; }
        MutableBuilder balance(long b) { balance = b; return this; }
        Account build() { return new Account(type, balance, frozen); }
    }

    /** Immutable builder: every "with" returns a NEW builder, so a shared base can't be corrupted. */
    record ImmutableBuilder(String type, long balance, boolean frozen) {
        static ImmutableBuilder anAccount() { return new ImmutableBuilder("SAVINGS", 100_000, false); }
        ImmutableBuilder asFrozen() { return new ImmutableBuilder(type, balance, true); }
        ImmutableBuilder withBalance(long b) { return new ImmutableBuilder(type, b, frozen); }
        Account build() { return new Account(type, balance, frozen); }
    }

    static final MutableBuilder SHARED = new MutableBuilder(); // "to save typing" in a test class
    static final ImmutableBuilder BASE = ImmutableBuilder.anAccount();

    public static void main(String[] args) {
        System.out.println("=== Challenge: The Shared Builder That Froze Every Account ===");
        Account a = SHARED.frozen().build();              // testFrozenAccountCannotWithdraw
        Account b = SHARED.balance(500_000).build();      // testLargeBalanceEarnsBonus, runs next
        System.out.println("mutable shared  : test 1 " + a + "\n                  test 2 " + b + "  <- frozen leaked in");

        Account c = BASE.asFrozen().build();
        Account d = BASE.withBalance(500_000).build();
        System.out.println("immutable base  : test 1 " + c + "\n                  test 2 " + d);
        System.out.println("Test order or parallel runs no longer change results.");
    }
}
