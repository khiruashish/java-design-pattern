package com.handbook.structural.flyweight.editor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Flyweight: a record gives value equality for free, which makes interning trivial. */
public record TextStyle(String font, int sizePt, boolean bold, boolean italic, String color) {

    private static final Map<TextStyle, TextStyle> POOL = new ConcurrentHashMap<>();

    public static TextStyle of(String font, int sizePt, boolean bold, boolean italic, String color) {
        TextStyle candidate = new TextStyle(font, sizePt, bold, italic, color);
        return POOL.computeIfAbsent(candidate, k -> k); // (1)!
    }

    public static int pooled() {
        return POOL.size();
    }
}
