package com.handbook.principles.solid.lsp.before;

/**
 * VIOLATES LSP: "a square IS-A rectangle" in geometry, but not in behaviour.
 * To keep its own invariant (all sides equal) it silently breaks the parent's contract.
 */
public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;     // surprise for any caller that only meant to change the width
    }

    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height;
    }
}
