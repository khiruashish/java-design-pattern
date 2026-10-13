package com.handbook.springref.resilience;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Resilience4j annotations on a Spring bean. Order of decoration (outermost first) by default:
 * Retry ( CircuitBreaker ( RateLimiter ( Bulkhead ( call ) ) ) ).
 * Instances "payment" are configured in application.yml (see the Circuit Breaker page).
 */
@Component
public class PaymentProviderClient {

    private final RestClient http;

    public PaymentProviderClient(RestClient.Builder builder) {
        this.http = builder.baseUrl("https://payments.example.com").build();
    }

    @Retry(name = "payment")
    @CircuitBreaker(name = "payment", fallbackMethod = "queueForLater")
    @RateLimiter(name = "payment")
    @Bulkhead(name = "payment")
    public String charge(String orderId, long amountPaise) {
        return http.post()
                .uri("/charges")
                .header("Idempotency-Key", orderId) // retries must not double-charge
                .body(new ChargeRequest(orderId, amountPaise))
                .retrieve()
                .body(String.class);
    }

    /** Same parameters plus the exception; called when the call fails or the circuit is open. */
    String queueForLater(String orderId, long amountPaise, Throwable cause) {
        return "QUEUED:" + orderId;
    }

    record ChargeRequest(String orderId, long amountPaise) {
    }
}
