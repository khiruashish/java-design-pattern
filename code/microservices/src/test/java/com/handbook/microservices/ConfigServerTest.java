package com.handbook.microservices;

import com.handbook.microservices.config.profiles.ConfigServer;
import com.handbook.microservices.config.validation.CheckoutConfig;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigServerTest {

    @Test
    void mostSpecificFileWins() {
        var server = new ConfigServer()
                .file("application", Map.of("k", "global"))
                .file("svc", Map.of("k", "svc"))
                .file("svc-prod", Map.of("k", "svc-prod"));
        assertThat(server.resolve("svc", "prod").properties()).containsEntry("k", "svc-prod");
        assertThat(server.resolve("svc", "dev").properties()).containsEntry("k", "svc");
        assertThat(server.resolve("other", "dev").properties()).containsEntry("k", "global");
    }

    @Test
    void invalidConfigFailsWithAllErrors() {
        assertThatThrownBy(() -> CheckoutConfig.bind(Map.of("checkout.max-items", "0")))
                .hasMessageContaining("max-items must be")
                .hasMessageContaining("payment-timeout is missing")
                .hasMessageContaining("payment-url is missing");
    }
}
