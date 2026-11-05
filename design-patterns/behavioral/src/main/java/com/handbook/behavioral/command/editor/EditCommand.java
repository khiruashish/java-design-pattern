package com.handbook.behavioral.command.editor;

/** Command: an edit as an object, with enough information to reverse itself. */
public interface EditCommand {

    void execute();

    void undo();

    String label();

    record Insert(Document doc, int position, String text) implements EditCommand {
        public void execute() { doc.insert(position, text); }
        public void undo() { doc.delete(position, text.length()); }
        public String label() { return "insert '" + text + "'"; }
    }

    final class Delete implements EditCommand {
        private final Document doc;
        private final int position;
        private final int length;
        private String removed; // (1)!

        public Delete(Document doc, int position, int length) {
            this.doc = doc;
            this.position = position;
            this.length = length;
        }

        public void execute() { removed = doc.delete(position, length); }
        public void undo() { doc.insert(position, removed); }
        public String label() { return "delete " + length + " chars"; }
    }
}
