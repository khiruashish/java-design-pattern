package com.handbook.behavioral.visitor.claims;

import com.handbook.behavioral.visitor.claims.Claim.*;

import java.util.List;

public class ClaimsDemo {

    public static void main(String[] args) {
        System.out.println("=== Health Insurance: Claim Adjudication ===");
        List<Claim> claims = List.of(
                new Consultation("CL-1", 1_200, "KMC-55123"),
                new Consultation("CL-2", 3_500, ""),
                new Surgery("CL-3", 420_000, true, 4),
                new Surgery("CL-4", 150_000, false, 0),
                new Pharmacy("CL-5", 2_400, true),
                new Pharmacy("CL-6", 26_000, false));

        for (Claim c : claims) {
            System.out.printf("%s %-12s INR %,7d -> %-38s fraud score %d%n", c.id(), c.getClass().getSimpleName(),
                    c.amountInr(), ClaimOperations.decide(c), ClaimOperations.fraudScore(c));
        }
    }
}
