package com.handbook.structural.flyweight.social;

import java.util.List;

public class HashtagDemo {

    public static void main(String[] args) {
        List<String> postsTags = List.of("#Java", "#java", " #JAVA ", "#SpringBoot", "#springboot", "#Java21");
        List<Hashtag> tags = postsTags.stream().map(Hashtag::of).toList();
        System.out.println("Tags used      : " + tags);
        System.out.println("Unique objects : " + Hashtag.cached());
        System.out.println("#Java == #JAVA ? " + (Hashtag.of("#Java") == Hashtag.of("#JAVA")));
    }
}
