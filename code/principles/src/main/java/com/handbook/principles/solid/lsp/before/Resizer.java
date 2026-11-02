package com.handbook.principles.solid.lsp.before;

/** Client code written against Rectangle. It is correct for Rectangle and wrong for Square. */
public class Resizer {
    public int resizeTo5x4(Rectangle r) {
        r.setWidth(5);
        r.setHeight(4);
        return r.area();         // caller reasonably expects 20
    }
}
