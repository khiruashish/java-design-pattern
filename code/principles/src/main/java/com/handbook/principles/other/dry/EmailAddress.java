package com.handbook.principles.other.dry;

import java.util.Locale;
import java.util.regex.Pattern;

/** DRY: the single, authoritative representation of "a valid email". Every caller reuses it. */
public record EmailAddress(String value) {
    private static final Pattern SHAPE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        if (value == null || !SHAPE.matcher(value.strip()).matches()) {
            throw new IllegalArgumentException("invalid email: " + value);
        }
        value = value.strip().toLowerCase(Locale.ROOT);
    }

    public static boolean isValid(String raw) {
        try {
            new EmailAddress(raw);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String domain() { return value.substring(value.indexOf('@') + 1); }
}
