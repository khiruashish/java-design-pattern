package com.handbook.microservices.bff.streaming;

import java.util.List;
import java.util.Map;

/** Downstream services shared by every client. They know nothing about screens. */
public final class StreamingBackends {

    private StreamingBackends() {
    }

    public record Title(String id, String name, String genre, int minutes, String synopsis,
                        Map<String, String> artwork, String trailerUrl) {
    }

    public static final class CatalogService {
        private final Map<String, Title> titles = Map.of(
                "T1", title("T1", "Monsoon Express", "Thriller"),
                "T2", title("T2", "City of Rain", "Drama"),
                "T3", title("T3", "Spice Route", "Documentary"),
                "T4", title("T4", "Byte Club", "Comedy"));

        private static Title title(String id, String name, String genre) {
            return new Title(id, name, genre, 52, "A long synopsis of " + name + " ...",
                    Map.of("320", "img/" + id + "-320.jpg", "1920", "img/" + id + "-1920.jpg"),
                    "video/" + id + "-trailer.m3u8");
        }

        public Title byId(String id) {
            return titles.get(id);
        }
    }

    public static final class ProgressService {
        /** Minutes watched per title for a profile. */
        public Map<String, Integer> progress(String profileId) {
            return Map.of("T2", 39);
        }
    }

    public static final class RecommendationService {
        public List<String> forProfile(String profileId) {
            return List.of("T1", "T3", "T4");
        }
    }
}
