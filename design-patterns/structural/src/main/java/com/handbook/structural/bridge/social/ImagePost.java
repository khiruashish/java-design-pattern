package com.handbook.structural.bridge.social;

import java.util.List;

public class ImagePost extends Post {

    private final String caption;
    private final List<String> images;

    public ImagePost(SocialPlatform platform, String caption, List<String> images) {
        super(platform);
        this.caption = caption;
        this.images = List.copyOf(images);
    }

    protected String text() { return caption; }

    @Override
    protected List<String> media() { return images; }
}
