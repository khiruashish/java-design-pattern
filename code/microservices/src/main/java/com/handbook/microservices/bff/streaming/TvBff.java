package com.handbook.microservices.bff.streaming;

import com.handbook.microservices.bff.streaming.StreamingBackends.CatalogService;
import com.handbook.microservices.bff.streaming.StreamingBackends.ProgressService;
import com.handbook.microservices.bff.streaming.StreamingBackends.RecommendationService;
import com.handbook.microservices.bff.streaming.StreamingBackends.Title;
import com.handbook.microservices.support.Json;

import java.util.Map;

/** Owned by the TV team: big artwork, an autoplay hero banner, more rows for a 10-foot screen. */
public final class TvBff {

    private final CatalogService catalog;
    private final ProgressService progress;
    private final RecommendationService recommendations;

    public TvBff(CatalogService catalog, ProgressService progress, RecommendationService recommendations) {
        this.catalog = catalog;
        this.progress = progress;
        this.recommendations = recommendations;
    }

    public Map<String, Object> homeScreen(String profileId) {
        var recs = recommendations.forProfile(profileId);
        Title hero = catalog.byId(recs.get(0));
        return Json.obj(
                "hero", Json.obj("name", hero.name(), "art", hero.artwork().get("1920"), // (1)!
                        "autoplay", hero.trailerUrl()),
                "continue", progress.progress(profileId).keySet().stream().map(id -> tile(catalog.byId(id))).toList(),
                "forYou", recs.stream().map(id -> tile(catalog.byId(id))).toList()); // (2)!
    }

    private static Map<String, Object> tile(Title t) {
        return Json.obj("id", t.id(), "name", t.name(), "genre", t.genre(), "art", t.artwork().get("1920"));
    }
}
