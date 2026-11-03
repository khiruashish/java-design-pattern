package com.handbook.enterprise.valueobject.telecom;

/** An Indian mobile number in canonical E.164 form. Parsing normalises every accepted spelling. */
public record PhoneNumber(String e164) {

    public PhoneNumber {
        if (!e164.matches("\\+91[6-9]\\d{9}")) {
            throw new IllegalArgumentException("not a canonical Indian mobile number: " + e164);
        }
    }

    public static PhoneNumber parse(String raw) { // (1)!
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.length() == 12 && digits.startsWith("91")) {
            digits = digits.substring(2);
        } else if (digits.length() == 11 && digits.startsWith("0")) {
            digits = digits.substring(1);
        }
        if (digits.length() != 10) {
            throw new IllegalArgumentException("cannot parse '" + raw + "'");
        }
        return new PhoneNumber("+91" + digits);
    }

    public String masked() {
        return e164.substring(0, 5) + "*****" + e164.substring(10);
    }
}
