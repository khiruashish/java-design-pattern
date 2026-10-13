package com.handbook.concurrency.dcl.manifest;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManifestDemo {

    public static void main(String[] args) {
        System.out.println("=== Streaming: Cached ETag for a Video Manifest ===");
        VideoManifest manifest = new VideoManifest("MONSOON-EXPRESS-S1E1", List.of("480p", "720p", "1080p", "4k"));
        Set<String> seen = ConcurrentHashMap.newKeySet();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                ex.submit(() -> seen.add(manifest.etag()));
            }
        }
        System.out.println("ETag            : " + manifest.etag());
        System.out.println("Distinct values : " + seen.size() + " (every thread saw the same ETag)");
        System.out.println("Computed at least once, far fewer than 1000 times: "
                + (VideoManifest.COMPUTATIONS.get() >= 1 && VideoManifest.COMPUTATIONS.get() < 1_000));
    }
}
