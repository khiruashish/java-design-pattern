package com.handbook.structural.proxy.streaming;

/** Virtual proxy: same interface, creates the real poster only when it is actually shown. */
public class LazyPoster implements Poster {

    private final String title;
    private HighResPoster real; // (1)!

    public LazyPoster(String title) {
        this.title = title;
    }

    @Override
    public String title() {
        return title; // (2)!
    }

    @Override
    public String render() {
        if (real == null) {
            real = new HighResPoster(title); // (3)!
        }
        return real.render();
    }
}
