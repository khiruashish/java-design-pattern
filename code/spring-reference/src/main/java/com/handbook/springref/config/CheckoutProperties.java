package com.handbook.springref.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

/**
 * Typed configuration bound from checkout.* properties (from application.yml, environment
 * variables, or a config server). Register with @EnableConfigurationProperties or
 * @ConfigurationPropertiesScan. Validation in the compact constructor makes startup fail fast;
 * with spring-boot-starter-validation you would use @Validated and @Min/@NotNull instead.
 */
@ConfigurationProperties(prefix = "checkout")
public record CheckoutProperties(int maxItems, Duration paymentTimeout, URI paymentUrl) {

    public CheckoutProperties {
        if (maxItems < 1 || maxItems > 500) {
            throw new IllegalArgumentException("checkout.max-items must be 1..500");
        }
        if (paymentTimeout == null || paymentTimeout.isZero()) {
            throw new IllegalArgumentException("checkout.payment-timeout is required");
        }
        if (paymentUrl == null || !"https".equals(paymentUrl.getScheme())) {
            throw new IllegalArgumentException("checkout.payment-url must be an https URL");
        }
    }
}
