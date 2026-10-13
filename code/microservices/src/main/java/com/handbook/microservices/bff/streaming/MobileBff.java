package com.handbook.microservices.bff.streaming;

import com.handbook.microservices.bff.streaming.StreamingBackends.CatalogService;
import com.handbook.microservices.bff.streaming.StreamingBackends.ProgressService;
import com.handbook.microservices.bff.streaming.StreamingBackends.RecommendationService;
import com.handbook.microservices.bff.streaming.StreamingBackends.Title;
import com.handbook.microservices.support.Json;

import java.util.Map;

/** Owned by the mobile team: small images, few fields, one round trip, metered-data friendly. */
public final class MobileBff {

    private final CatalogService catalog;
    private final ProgressService progress;
    private final RecommendationService recommendations;

    public MobileBff(CatalogService catalog, ProgressService progress, RecommendationService recommendations) {
        this.catalog = catalog;
        this.progress = progress;
        this.recommendations = recommendations;
    }

    public Map<String, Object> homeScreen(String profileId) {
        var continueWatching = progress.progress(profileId).entrySet().stream()
                .map(e -> {
                    Title t = catalog.byId(e.getKey());
                    return card(t, Json.obj("percent", e.getValue() * 100 / t.minutes())); // (1)!
                }).toList();
        var forYou = recommendations.forProfile(profileId).stream()
                .limit(2) // (2)!
                .map(id -> card(catalog.byId(id), Json.obj()))
                .toList();
        return Json.obj("continue", continueWatching, "forYou", forYou);
    }

    private static Map<String, Object> card(Title t, Map<String, Object> extra) {
        Map<String, Object> card = Json.obj("id", t.id(), "name", t.name(), "img", t.artwork().get("320")); // (3)!
        card.putAll(extra);
        return card;
    }
}
