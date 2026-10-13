package com.handbook.behavioral.visitor.claims;

import com.handbook.behavioral.visitor.claims.Claim.*;

/** Each operation is one method with an exhaustive switch: a "visitor" without boilerplate. */
public final class ClaimOperations {

    private ClaimOperations() {
    }

    public static String decide(Claim c) {
        return switch (c) { // (1)!
            case Consultation x when x.amountInr() <= 2_000 -> "AUTO-APPROVE";
            case Consultation x -> "REVIEW (consultation above INR 2000)";
            case Surgery s when !s.preAuthorized() -> "REJECT (no pre-authorization)";
            case Surgery s -> "APPROVE " + Math.min(s.amountInr(), 300_000) + " (sum insured cap)";
            case Pharmacy p when !p.hasPrescription() -> "REJECT (no prescription)";
            case Pharmacy p -> "AUTO-APPROVE";
        };
    }

    public static int fraudScore(Claim c) {
        return switch (c) {
            case Consultation x -> x.doctorRegNo().isBlank() ? 70 : 5;
            case Surgery s -> s.daysAdmitted() == 0 && s.amountInr() > 100_000 ? 60 : 10;
            case Pharmacy p -> p.amountInr() > 20_000 ? 40 : 5;
        };
    }
}
