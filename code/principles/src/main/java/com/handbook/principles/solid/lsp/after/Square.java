package com.handbook.principles.solid.lsp.after;

public record Square(int side) implements Shape {
    public int area() { return side * side; }
}
