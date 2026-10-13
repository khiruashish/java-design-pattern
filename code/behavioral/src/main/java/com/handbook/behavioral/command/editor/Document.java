package com.handbook.behavioral.command.editor;

/** Receiver: knows how to change text; knows nothing about undo. */
public class Document {

    private final StringBuilder text = new StringBuilder();

    public void insert(int position, String s) {
        text.insert(position, s);
    }

    public String delete(int position, int length) {
        String removed = text.substring(position, position + length);
        text.delete(position, position + length);
        return removed;
    }

    public int length() {
        return text.length();
    }

    @Override
    public String toString() {
        return '"' + text.toString() + '"';
    }
}
