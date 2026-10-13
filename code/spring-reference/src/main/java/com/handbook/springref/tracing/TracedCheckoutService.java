package com.handbook.springref.tracing;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * With micrometer-tracing-bridge-otel on the classpath, Spring Boot instruments incoming HTTP
 * requests and the auto-configured RestClient.Builder, so traceparent is propagated for free.
 * Custom spans for business steps use the Observation API.
 */
@Service
public class TracedCheckoutService {

    private final RestClient payments;
    private final ObservationRegistry observations;

    public TracedCheckoutService(RestClient.Builder builder, ObservationRegistry observations) {
        this.payments = builder.baseUrl("http://payment-service").build(); // builder carries tracing
        this.observations = observations;
    }

    public String checkout(String orderId) {
        long total = Observation.createNotStarted("checkout.price-cart", observations)
                .lowCardinalityKeyValue("channel", "app") // becomes a span tag and a metric dimension
                .observe(() -> priceCart(orderId));
        return payments.post().uri("/charges/{id}", orderId)
                .body(total)
                .retrieve()
                .body(String.class); // child span + traceparent header added automatically
    }

    private long priceCart(String orderId) {
        return 79_800;
    }
}
