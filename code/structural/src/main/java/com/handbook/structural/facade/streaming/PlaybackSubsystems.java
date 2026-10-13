package com.handbook.structural.facade.streaming;

import java.util.List;

/** Complex subsystems a player needs before the first frame. */
public final class PlaybackSubsystems {

    private PlaybackSubsystems() {
    }

    public static class EntitlementService {
        public boolean canWatch(String userId, String titleId) {
            return !titleId.startsWith("PREMIUM") || userId.endsWith("-premium");
        }
    }

    public static class DrmLicenseServer {
        public String license(String titleId, String device) {
            return (device.equals("browser") ? "Widevine-L3" : "Widevine-L1") + ":" + titleId;
        }
    }

    public static class CdnSelector {
        public String bestEdge(String city) {
            return switch (city) {
                case "Mumbai" -> "edge-bom-2";
                case "Chennai" -> "edge-maa-1";
                default -> "edge-del-1";
            };
        }
    }

    public static class BitrateLadder {
        public String startingRendition(int bandwidthKbps, boolean supportsHdr) {
            if (bandwidthKbps >= 15_000) {
                return supportsHdr ? "2160p HDR" : "2160p";
            }
            return bandwidthKbps >= 5_000 ? "1080p" : bandwidthKbps >= 1_500 ? "720p" : "480p";
        }
    }

    public static class SubtitleService {
        public List<String> tracks(String titleId, String locale) {
            return locale.equals("hi-IN") ? List.of("hi", "en") : List.of("en");
        }
    }
}
