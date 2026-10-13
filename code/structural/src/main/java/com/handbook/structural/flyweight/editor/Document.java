package com.handbook.structural.flyweight.editor;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/** A collaborative document: each character points to a shared style. */
public class Document {

    private record StyledChar(char ch, TextStyle style) { // extrinsic: the char; intrinsic: the style
    }

    private final List<StyledChar> chars = new ArrayList<>();

    public Document type(String text, TextStyle style) {
        for (char c : text.toCharArray()) {
            chars.add(new StyledChar(c, style));
        }
        return this;
    }

    public int length() {
        return chars.size();
    }

    public int distinctStyleObjects() {
        IdentityHashMap<TextStyle, Boolean> seen = new IdentityHashMap<>();
        chars.forEach(c -> seen.put(c.style(), true));
        return seen.size();
    }
}
