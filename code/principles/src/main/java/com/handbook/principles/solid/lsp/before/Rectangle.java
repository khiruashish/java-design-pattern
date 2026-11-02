package com.handbook.principles.solid.lsp.before;

/** A mutable rectangle. Its implicit contract: setWidth changes ONLY the width. */
public class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) { this.width = width; }

    public void setHeight(int height) { this.height = height; }

    public int area() { return width * height; }
}
