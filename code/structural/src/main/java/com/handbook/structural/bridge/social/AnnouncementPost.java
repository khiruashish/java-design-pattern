package com.handbook.structural.bridge.social;

public class AnnouncementPost extends Post {

    private final String headline;
    private final String body;

    public AnnouncementPost(SocialPlatform platform, String headline, String body) {
        super(platform);
        this.headline = headline;
        this.body = body;
    }

    protected String text() {
        return headline.toUpperCase() + " - " + body + " #launch";
    }
}
