package com.handbook.microservices;

import com.handbook.microservices.gateway.grocery.MobileGateway;
import com.handbook.microservices.gateway.openbanking.OpenBankingGateway;
import com.handbook.microservices.support.FakeClock;
import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiGatewayTest {

    @Test
    void gatewayStripsPrefixAndPropagatesIdentity() {
        MobileGateway gw = new MobileGateway().route("/orders", r -> Response.of(200, r.path() + "|" + r.header("X-Customer-Id")), true);
        Response res = gw.handle(Request.get("/orders/1", Map.of("Authorization", "Bearer tok-asha")));
        assertThat(res.body()).isEqualTo("/1|CUST-42");
        assertThat(res.headers()).containsKey("X-Request-Id");
    }

    @Test
    void quotaResetsEachMinute() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        var gw = new OpenBankingGateway(Map.of("k", new OpenBankingGateway.Partner("p", 1)), r -> Response.of(200, "ok"), clock);
        var req = Request.get("/x", Map.of("X-Api-Key", "k"));
        assertThat(gw.handle(req).status()).isEqualTo(200);
        assertThat(gw.handle(req).status()).isEqualTo(429);
        clock.advanceMillis(60_000);
        assertThat(gw.handle(req).status()).isEqualTo(200);
    }
}
