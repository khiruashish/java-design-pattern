package com.handbook.structural.bridge.social;

import java.util.List;

/** Abstraction: what kind of post. Platform limits come through the bridge. */
public abstract class Post {

    protected final SocialPlatform platform;

    protected Post(SocialPlatform platform) {
        this.platform = platform;
    }

    protected abstract String text();

    protected List<String> media() {
        return List.of();
    }

    public final String share() {
        String text = text();
        if (text.length() > platform.maxLength()) {
            text = text.substring(0, platform.maxLength() - 1) + "…"; // adapt to the platform's limit
        }
        return platform.name() + ": " + platform.publish(text, media());
    }
}
