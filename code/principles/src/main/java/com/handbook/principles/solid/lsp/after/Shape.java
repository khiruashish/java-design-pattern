package com.handbook.principles.solid.lsp.after;

/**
 * The fix: model what the shapes actually share (they have an area) and make them immutable.
 * There is no setter whose contract a subtype could break.
 */
public sealed interface Shape permits Rectangle, Square {
    int area();
}
