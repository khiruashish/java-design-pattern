package com.handbook.concurrency.dcl.manifest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Immutable manifest with a lazily cached content hash (used as a CDN cache key).
 * "Racy single-check": no lock, no volatile. Safe ONLY because the value is
 * immutable, cheap to recompute and always the same; worst case it is computed twice.
 */
public final class VideoManifest {

    static final AtomicInteger COMPUTATIONS = new AtomicInteger();

    private final String titleId;
    private final List<String> renditions;
    private String etag; // (1)!

    public VideoManifest(String titleId, List<String> renditions) {
        this.titleId = titleId;
        this.renditions = List.copyOf(renditions);
    }

    public String etag() {
        String e = etag; // (2)!
        if (e == null) {
            e = computeEtag();
            etag = e;
        }
        return e;
    }

    private String computeEtag() {
        COMPUTATIONS.incrementAndGet();
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest((titleId + renditions).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 8); // a String is immutable: safe to publish racily
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
