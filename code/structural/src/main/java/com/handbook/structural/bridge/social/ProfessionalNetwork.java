package com.handbook.structural.bridge.social;

import java.util.List;

public class ProfessionalNetwork implements SocialPlatform {

    public String name() { return "ProNet"; }

    public int maxLength() { return 3000; }

    public String publish(String text, List<String> media) {
        return "POST /ugcPosts visibility=PUBLIC text(" + text.length() + " chars) media=" + media;
    }
}
