package com.handbook.behavioral.nullobject.viewer;

import java.util.Map;

public class ViewerDemo {

    static final Map<String, Viewer> SESSIONS = Map.of("sess-42", new Viewer.Member("Kavya"));

    static Viewer currentViewer(String sessionId) {
        return SESSIONS.getOrDefault(sessionId, Viewer.GUEST); // never null
    }

    static void watch(String sessionId, String title) {
        Viewer v = currentViewer(sessionId);
        v.recordWatched(title);
        System.out.println("  " + v.greeting() + " | next: " + v.recommendations()
                + " | download: " + (v.canDownload() ? "yes" : "sign in required"));
    }

    public static void main(String[] args) {
        System.out.println("=== Streaming: Members and Guests, One Code Path ===");
        System.out.println("Signed-in member:");
        watch("sess-42", "City of Rain");
        System.out.println("Guest (no session):");
        watch("sess-expired", "City of Rain");
    }
}
