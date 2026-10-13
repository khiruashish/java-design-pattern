package com.handbook.structural.bridge.social;

import java.util.List;

/** Implementor: each platform's API and limits. */
public interface SocialPlatform {

    String name();

    int maxLength();

    String publish(String text, List<String> mediaUrls);
}
