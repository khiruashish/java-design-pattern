package com.handbook.behavioral.command.editor;

import java.util.ArrayDeque;
import java.util.Deque;

/** Invoker: runs commands and keeps the undo/redo history. */
public class Editor {

    private final Deque<EditCommand> undoStack = new ArrayDeque<>();
    private final Deque<EditCommand> redoStack = new ArrayDeque<>();

    public void run(EditCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // (1)!
    }

    public String undo() {
        if (undoStack.isEmpty()) {
            return "nothing to undo";
        }
        EditCommand c = undoStack.pop();
        c.undo();
        redoStack.push(c);
        return "undid " + c.label();
    }

    public String redo() {
        if (redoStack.isEmpty()) {
            return "nothing to redo";
        }
        EditCommand c = redoStack.pop();
        c.execute();
        undoStack.push(c);
        return "redid " + c.label();
    }
}
