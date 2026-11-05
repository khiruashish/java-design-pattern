package com.handbook.microservices.ambassador.legacy;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Runs next to the legacy app (localhost:9000). Adds what the app can't: auth, retries, timeouts, logs. */
public final class PartnerAmbassador implements Function<Request, Response> {

    private final Function<Request, Response> remotePartner;
    private final String apiKey;
    private final int maxAttempts;
    public final List<String> log = new ArrayList<>();

    public PartnerAmbassador(Function<Request, Response> remotePartner, String apiKey, int maxAttempts) {
        this.remotePartner = remotePartner;
        this.apiKey = apiKey;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public Response apply(Request fromLegacyApp) {
        Request outbound = fromLegacyApp
                .withPath("https://api.partner-bank.example" + fromLegacyApp.path()) // (1)!
                .withHeader("Authorization", "ApiKey " + apiKey)                    // (2)!
                .withHeader("X-Request-Id", "req-" + Math.abs(fromLegacyApp.path().hashCode() % 10_000));
        Response last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = remotePartner.apply(outbound);
            log.add("attempt " + attempt + " -> " + last.status());
            if (last.status() < 500) {
                return last;
            }
        }
        return last; // (3)!
    }
}
