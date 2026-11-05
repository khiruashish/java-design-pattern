package com.handbook.microservices.ambassador.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

/** Coding challenge solution: an ambassador that caches an OAuth2 client-credentials token and refreshes it early. */
public final class TokenAmbassador {

    public record Token(String value, Instant expiresAt) { }

    private final Supplier<Token> authServer;
    private final Clock clock;
    private final Duration refreshMargin;
    private Token cached;
    private int tokenRequests;

    public TokenAmbassador(Supplier<Token> authServer, Clock clock, Duration refreshMargin) {
        this.authServer = authServer;
        this.clock = clock;
        this.refreshMargin = refreshMargin;
    }

    public synchronized String authorizationHeader() {
        if (cached == null || !clock.instant().isBefore(cached.expiresAt().minus(refreshMargin))) {
            cached = authServer.get();
            tokenRequests++;
        }
        return "Bearer " + cached.value();
    }

    public int tokenRequests() {
        return tokenRequests;
    }
}
