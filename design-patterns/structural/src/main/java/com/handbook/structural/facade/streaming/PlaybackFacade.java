package com.handbook.structural.facade.streaming;

import com.handbook.structural.facade.streaming.PlaybackSubsystems.*;

import java.util.List;

/** Facade used by every player app (TV, mobile, web): one call returns everything to start playback. */
public class PlaybackFacade {

    public record PlaybackSession(String license, String cdnEdge, String rendition, List<String> subtitles) {
    }

    public record Client(String userId, String device, String city, int bandwidthKbps, boolean hdr, String locale) {
    }

    private final EntitlementService entitlements = new EntitlementService();
    private final DrmLicenseServer drm = new DrmLicenseServer();
    private final CdnSelector cdn = new CdnSelector();
    private final BitrateLadder ladder = new BitrateLadder();
    private final SubtitleService subtitles = new SubtitleService();

    public PlaybackSession start(String titleId, Client c) {
        if (!entitlements.canWatch(c.userId(), titleId)) {
            throw new SecurityException(c.userId() + " is not entitled to " + titleId);
        }
        return new PlaybackSession(
                drm.license(titleId, c.device()),
                cdn.bestEdge(c.city()),
                ladder.startingRendition(c.bandwidthKbps(), c.hdr()),
                subtitles.tracks(titleId, c.locale()));
    }
}
