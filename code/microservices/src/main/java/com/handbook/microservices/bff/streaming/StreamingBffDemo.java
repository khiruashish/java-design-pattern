package com.handbook.microservices.bff.streaming;

import com.handbook.microservices.bff.streaming.StreamingBackends.CatalogService;
import com.handbook.microservices.bff.streaming.StreamingBackends.ProgressService;
import com.handbook.microservices.bff.streaming.StreamingBackends.RecommendationService;
import com.handbook.microservices.support.Json;

public class StreamingBffDemo {

    public static void main(String[] args) {
        var catalog = new CatalogService();
        var progress = new ProgressService();
        var recs = new RecommendationService();

        System.out.println("=== Streaming: one backend, two BFFs ===");
        String mobile = Json.of(new MobileBff(catalog, progress, recs).homeScreen("P-7"));
        String tv = Json.of(new TvBff(catalog, progress, recs).homeScreen("P-7"));

        System.out.println("GET /mobile/home ->");
        System.out.println("  " + mobile);
        System.out.println("GET /tv/home ->");
        System.out.println("  " + tv);
        System.out.println("Payload size: mobile " + mobile.length() + " chars, tv " + tv.length() + " chars");
        System.out.println("Same 3 backend services, each screen gets exactly what it renders.");
    }
}
