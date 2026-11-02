package com.handbook.principles.solid.lsp.after;

public record Rectangle(int width, int height) implements Shape {
    public int area() { return width * height; }

    /** "Resizing" returns a new value instead of mutating, so no subtype can surprise the caller. */
    public Rectangle withSize(int newWidth, int newHeight) { return new Rectangle(newWidth, newHeight); }
}
