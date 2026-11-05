package com.handbook.behavioral.visitor.claims;

/** Sealed hierarchy: Java 21 pattern matching gives Visitor-style dispatch without accept(). */
public sealed interface Claim {

    String id();

    int amountInr();

    record Consultation(String id, int amountInr, String doctorRegNo) implements Claim { }

    record Surgery(String id, int amountInr, boolean preAuthorized, int daysAdmitted) implements Claim { }

    record Pharmacy(String id, int amountInr, boolean hasPrescription) implements Claim { }
}
