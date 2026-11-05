package com.handbook.structural.bridge.social;

import java.util.List;

public class Microblog implements SocialPlatform {

    public String name() { return "Microblog"; }

    public int maxLength() { return 80; } // short for the demo; real limit 280

    public String publish(String text, List<String> media) {
        return "POST /statuses text(" + text.length() + " chars) media=" + media.size();
    }
}
