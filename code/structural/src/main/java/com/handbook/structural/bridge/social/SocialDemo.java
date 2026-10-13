package com.handbook.structural.bridge.social;

import java.util.List;

public class SocialDemo {

    public static void main(String[] args) {
        System.out.println("=== Social Media Scheduler: Post Types x Platforms ===");
        String body = "Our grocery app now delivers in 10 minutes across 40 new pin codes in Pune.";
        for (SocialPlatform p : List.of(new Microblog(), new ProfessionalNetwork())) {
            System.out.println(new AnnouncementPost(p, "Now in Pune", body).share());
            System.out.println(new ImagePost(p, "Team photo from launch day", List.of("team.jpg")).share());
        }
    }
}
