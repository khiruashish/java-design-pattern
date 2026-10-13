package com.handbook.microservices.config.validation;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Coding challenge: bind raw string properties into a typed, validated record and FAIL FAST at
 * startup, reporting every problem at once, instead of failing on the first checkout at 2 a.m.
 */
public record CheckoutConfig(int maxItems, Duration paymentTimeout, URI paymentUrl) {

    public static final class InvalidConfigException extends RuntimeException {
        InvalidConfigException(List<String> errors) {
            super("invalid configuration:\n  - " + String.join("\n  - ", errors));
        }
    }

    public static CheckoutConfig bind(Map<String, String> p) {
        List<String> errors = new ArrayList<>();
        Integer maxItems = parse(p, "checkout.max-items", Integer::valueOf, errors);
        Duration timeout = parse(p, "checkout.payment-timeout", Duration::parse, errors);
        URI url = parse(p, "checkout.payment-url", URI::create, errors);

        if (maxItems != null && (maxItems < 1 || maxItems > 500)) {
            errors.add("checkout.max-items must be 1..500 (was " + maxItems + ")");
        }
        if (timeout != null && (timeout.isZero() || timeout.compareTo(Duration.ofSeconds(10)) > 0)) {
            errors.add("checkout.payment-timeout must be > 0 and <= PT10S (was " + timeout + ")");
        }
        if (url != null && !"https".equals(url.getScheme())) {
            errors.add("checkout.payment-url must use https (was " + url + ")");
        }
        if (!errors.isEmpty()) {
            throw new InvalidConfigException(errors);
        }
        return new CheckoutConfig(maxItems, timeout, url);
    }

    private static <T> T parse(Map<String, String> p, String key, Function<String, T> parser,
                               List<String> errors) {
        String raw = p.get(key);
        if (raw == null) {
            errors.add(key + " is missing");
            return null;
        }
        try {
            return parser.apply(raw.trim());
        } catch (RuntimeException e) {
            errors.add(key + " is not valid: '" + raw + "'");
            return null;
        }
    }
}
