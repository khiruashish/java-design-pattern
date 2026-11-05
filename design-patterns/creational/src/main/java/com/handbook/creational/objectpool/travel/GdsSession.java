package com.handbook.creational.objectpool.travel;

/**
 * A session with an airline Global Distribution System. Logging in is slow and the
 * airline licenses only a fixed number of concurrent sessions.
 */
public class GdsSession {

    private final String id;
    private int uses;

    GdsSession(String id) {
        this.id = id;
        System.out.println("  [gds] login " + id + " (2 s handshake, uses 1 licence)");
    }

    public String search(String route) {
        uses++;
        return id + " fares " + route + " (use " + uses + ")";
    }

    String id() { return id; }

    int uses() { return uses; }

    void logout() {
        System.out.println("  [gds] logout " + id + " after " + uses + " uses (refresh policy)");
    }
}
