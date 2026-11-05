package com.handbook.creational.builder.api;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/** Challenge solution: immutable request with defaults, validation and toBuilder(). */
public record ApiRequest(String method, String url, Map<String, String> headers,
                         String body, Duration timeout, int maxRetries) {

    public ApiRequest {
        headers = Map.copyOf(headers);
    }

    public static Builder builder(String url) {
        return new Builder(url);
    }

    public Builder toBuilder() {
        Builder b = new Builder(url);
        b.method = method;
        b.headers.putAll(headers);
        b.body = body;
        b.timeout = timeout;
        b.maxRetries = maxRetries;
        return b;
    }

    public static final class Builder {
        private final String url;
        private String method = "GET";
        private final Map<String, String> headers = new LinkedHashMap<>();
        private String body;
        private Duration timeout = Duration.ofSeconds(2);
        private int maxRetries = 0;

        private Builder(String url) {
            this.url = url;
        }

        public Builder post(String body) { this.method = "POST"; this.body = body; return this; }
        public Builder header(String name, String value) { headers.put(name, value); return this; }
        public Builder timeout(Duration timeout) { this.timeout = timeout; return this; }
        public Builder retries(int maxRetries) { this.maxRetries = maxRetries; return this; }

        public ApiRequest build() {
            if (!url.startsWith("https://")) {
                throw new IllegalStateException("Only HTTPS URLs are allowed: " + url);
            }
            if (method.equals("POST") && maxRetries > 0 && !headers.containsKey("Idempotency-Key")) {
                throw new IllegalStateException("Retried POSTs need an Idempotency-Key header");
            }
            return new ApiRequest(method, url, headers, body, timeout, maxRetries);
        }
    }
}
