package com.handbook.structural.facade.streaming;

import com.handbook.structural.facade.streaming.PlaybackFacade.Client;

public class PlaybackDemo {

    public static void main(String[] args) {
        System.out.println("=== Streaming: Press Play ===");
        PlaybackFacade playback = new PlaybackFacade();

        Client tv = new Client("u42-premium", "smart-tv", "Mumbai", 25_000, true, "hi-IN");
        Client phone = new Client("u77", "browser", "Delhi", 3_000, false, "en-IN");

        System.out.println("TV    : " + playback.start("PREMIUM-SERIES-S2E1", tv));
        System.out.println("Phone : " + playback.start("MOVIE-1207", phone));
        try {
            playback.start("PREMIUM-SERIES-S2E1", phone);
        } catch (SecurityException e) {
            System.out.println("Phone : blocked - " + e.getMessage());
        }
    }
}
