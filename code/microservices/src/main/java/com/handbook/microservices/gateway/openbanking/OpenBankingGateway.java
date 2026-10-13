package com.handbook.microservices.gateway.openbanking;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Partner-facing gateway: API keys, per-partner quotas, rate-limit headers. */
public class OpenBankingGateway {

    public record Partner(String name, int requestsPerMinute) {
    }

    private final Map<String, Partner> apiKeys;
    private final Function<Request, Response> accountsService;
    private final Clock clock;
    private final Map<String, Integer> used = new HashMap<>();
    private final Map<String, Instant> windowStart = new HashMap<>();

    public OpenBankingGateway(Map<String, Partner> apiKeys, Function<Request, Response> accountsService, Clock clock) {
        this.apiKeys = apiKeys;
        this.accountsService = accountsService;
        this.clock = clock;
    }

    public Response handle(Request req) {
        Partner partner = apiKeys.get(req.header("X-Api-Key"));
        if (partner == null) {
            return Response.of(401, "invalid API key");
        }
        Instant minute = clock.instant().truncatedTo(ChronoUnit.MINUTES); // (1)!
        if (!minute.equals(windowStart.get(partner.name()))) {
            windowStart.put(partner.name(), minute);
            used.put(partner.name(), 0);
        }
        int count = used.merge(partner.name(), 1, Integer::sum);
        int remaining = Math.max(0, partner.requestsPerMinute() - count);
        if (count > partner.requestsPerMinute()) {
            long retryAfter = 60 - clock.instant().getEpochSecond() % 60;
            return Response.of(429, "quota exceeded").withHeader("Retry-After", String.valueOf(retryAfter)); // (2)!
        }
        return accountsService.apply(req.withHeader("X-Partner", partner.name()))
                .withHeader("X-RateLimit-Remaining", String.valueOf(remaining)); // (3)!
    }
}
