package com.handbook.microservices;

import com.handbook.microservices.ambassador.legacy.PartnerAmbassador;
import com.handbook.microservices.ambassador.shardproxy.CacheShardProxy;
import com.handbook.microservices.ambassador.token.TokenAmbassador;
import com.handbook.microservices.ambassador.token.TokenAmbassador.Token;
import com.handbook.microservices.support.FakeClock;
import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AmbassadorTest {

    @Test
    void ambassadorAddsAuthAndRetriesServerErrors() {
        int[] calls = {0};
        PartnerAmbassador a = new PartnerAmbassador(r -> {
            calls[0]++;
            assertThat(r.header("Authorization")).isEqualTo("ApiKey k");
            return calls[0] == 1 ? Response.of(502, "") : Response.of(200, "ok");
        }, "k", 3);
        assertThat(a.apply(Request.get("/x", Map.of())).status()).isEqualTo(200);
        assertThat(calls[0]).isEqualTo(2);
    }

    @Test
    void clientErrorsAreNotRetried() {
        int[] calls = {0};
        PartnerAmbassador a = new PartnerAmbassador(r -> {
            calls[0]++;
            return Response.of(404, "");
        }, "k", 3);
        a.apply(Request.get("/x", Map.of()));
        assertThat(calls[0]).isEqualTo(1);
    }

    @Test
    void shardProxyRoundTrips() {
        CacheShardProxy p = new CacheShardProxy(4);
        p.set("k", "v");
        assertThat(p.get("k")).isEqualTo("v");
    }

    @Test
    void tokenIsReusedUntilTheRefreshMargin() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        TokenAmbassador t = new TokenAmbassador(() -> new Token("t", clock.instant().plus(Duration.ofMinutes(10))), clock, Duration.ofMinutes(2));
        t.authorizationHeader();
        clock.advance(Duration.ofMinutes(7));
        t.authorizationHeader();
        assertThat(t.tokenRequests()).isEqualTo(1);
        clock.advance(Duration.ofMinutes(1));
        t.authorizationHeader();
        assertThat(t.tokenRequests()).isEqualTo(2);
    }
}
