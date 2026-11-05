package com.handbook.structural.proxy.streaming;

/** Real subject: expensive to create (downloads and decodes an 8 MB image). */
public class HighResPoster implements Poster {

    private final String title;
    private final byte[] pixels;

    public HighResPoster(String title) {
        this.title = title;
        System.out.println("  [download] 8 MB poster for '" + title + "'");
        this.pixels = new byte[8 * 1024]; // scaled down for the demo
    }

    public String title() { return title; }

    public String render() { return "[poster: " + title + ", " + pixels.length / 1024 + "K px]"; }
}
