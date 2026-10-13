package com.handbook.microservices.idempotency.apikey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Server side of the Idempotency-Key header (as used by payment APIs): the first request with a key
 * is executed and its response stored; retries with the same key get the stored response. Reusing a
 * key with a different request body is a client bug and is rejected.
 * Simplified for one thread: in production, first insert the key as IN_PROGRESS with a unique
 * constraint, so a concurrent retry gets 409 Conflict instead of executing the operation twice.
 */
public final class IdempotencyKeyStore {

    public record StoredResponse(String requestFingerprint, int status, String body) {
    }

    private final Map<String, StoredResponse> responses = new ConcurrentHashMap<>();

    public StoredResponse execute(String key, String requestBody, Supplier<StoredResponse> operation) {
        String fingerprint = Integer.toHexString(requestBody.hashCode()); // (1)!
        StoredResponse previous = responses.get(key);
        if (previous != null) {
            if (!previous.requestFingerprint().equals(fingerprint)) {
                return new StoredResponse(fingerprint, 422, "Idempotency-Key reused with a different request"); // (2)!
            }
            return previous; // (3)!
        }
        StoredResponse fresh = operation.get();
        StoredResponse stored = new StoredResponse(fingerprint, fresh.status(), fresh.body());
        responses.put(key, stored);
        return stored;
    }
}
