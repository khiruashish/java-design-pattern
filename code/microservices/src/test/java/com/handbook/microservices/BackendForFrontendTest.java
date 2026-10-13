package com.handbook.microservices;

import com.handbook.microservices.bff.partner.PartnerAccountBff;
import com.handbook.microservices.bff.partner.PartnerAccountBff.InternalAccount;
import com.handbook.microservices.bff.streaming.MobileBff;
import com.handbook.microservices.bff.streaming.StreamingBackends.CatalogService;
import com.handbook.microservices.bff.streaming.StreamingBackends.ProgressService;
import com.handbook.microservices.bff.streaming.StreamingBackends.RecommendationService;
import com.handbook.microservices.support.Json;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BackendForFrontendTest {

    @Test
    void mobileHomeUsesSmallArtworkOnly() {
        String json = Json.of(new MobileBff(new CatalogService(), new ProgressService(), new RecommendationService())
                .homeScreen("P-1"));
        assertThat(json).contains("-320.jpg").doesNotContain("-1920.jpg").doesNotContain("synopsis");
    }

    @Test
    void partnerBffNeverLeaksInternalFields() {
        var bff = new PartnerAccountBff(n -> new InternalAccount(n, "Asha Rao", 100, "INR", "BLR-017", 12, "VERIFIED"),
                Set.of("12345678"));
        String json = Json.of(bff.account("v2", "12345678"));
        assertThat(json).contains("XXXX5678").doesNotContain("BLR-017").doesNotContain("12345678\"");
        assertThat(Json.of(bff.account("v2", "99999999"))).contains("NO_CONSENT");
    }
}
