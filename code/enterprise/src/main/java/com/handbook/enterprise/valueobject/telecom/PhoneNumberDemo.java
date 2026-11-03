package com.handbook.enterprise.valueobject.telecom;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PhoneNumberDemo {

    public static void main(String[] args) {
        System.out.println("=== Telecom: De-duplicating Subscribers by PhoneNumber ===");
        List<String> signups = List.of("+91 98450 12345", "098450-12345", "9845012345", "91-9845012345", "+91 70221 99887");
        Set<PhoneNumber> unique = new LinkedHashSet<>();
        for (String raw : signups) {
            unique.add(PhoneNumber.parse(raw));
        }
        System.out.println(signups.size() + " raw sign-ups -> " + unique.size() + " subscribers: " + unique.stream().map(PhoneNumber::e164).toList());
        System.out.println("Masked for SMS logs: " + unique.stream().map(PhoneNumber::masked).toList());
        for (String bad : List.of("12345", "+91 12345 67890")) {
            try {
                PhoneNumber.parse(bad);
            } catch (IllegalArgumentException e) {
                System.out.println("Rejected: " + e.getMessage());
            }
        }
    }
}
