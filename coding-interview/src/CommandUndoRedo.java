import java.util.ArrayDeque;
import java.util.Deque;

/** Problem: a text editor with insert/delete and unlimited undo/redo. */
public class CommandUndoRedo {

    static final class Document {
        private final StringBuilder text = new StringBuilder();
        void insert(int pos, String s) { text.insert(pos, s); }
        String delete(int pos, int len) { String removed = text.substring(pos, pos + len); text.delete(pos, pos + len); return removed; }
        int length() { return text.length(); }
        @Override public String toString() { return text.toString(); }
    }

    /** Each command knows how to do AND undo itself. */
    interface Command { void execute(); void undo(); String name(); }

    record Insert(Document doc, int pos, String s) implements Command {
        public void execute() { doc.insert(pos, s); }
        public void undo() { doc.delete(pos, s.length()); }
        public String name() { return "insert '" + s + "'"; }
    }

    static final class Delete implements Command {
        private final Document doc; private final int pos, len;
        private String removed;                       // state captured at execute time, needed for undo
        Delete(Document doc, int pos, int len) { this.doc = doc; this.pos = pos; this.len = len; }
        public void execute() { removed = doc.delete(pos, len); }
        public void undo() { doc.insert(pos, removed); }
        public String name() { return "delete " + len + " chars"; }
    }

    /** The invoker: two stacks. A new command clears the redo stack (the history branches). */
    static final class Editor {
        final Document doc = new Document();
        private final Deque<Command> undo = new ArrayDeque<>(), redo = new ArrayDeque<>();

        void run(Command c) { c.execute(); undo.push(c); redo.clear(); }
        boolean undo() { if (undo.isEmpty()) return false; Command c = undo.pop(); c.undo(); redo.push(c); return true; }
        boolean redo() { if (redo.isEmpty()) return false; Command c = redo.pop(); c.execute(); undo.push(c); return true; }
    }

    public static void main(String[] args) {
        Editor ed = new Editor();
        ed.run(new Insert(ed.doc, 0, "Hello"));
        ed.run(new Insert(ed.doc, 5, " World"));
        ed.run(new Delete(ed.doc, 0, 6));
        System.out.println("after 3 commands : '" + ed.doc + "'");
        ed.undo();
        System.out.println("undo             : '" + ed.doc + "'");
        ed.undo();
        System.out.println("undo             : '" + ed.doc + "'");
        ed.redo();
        System.out.println("redo             : '" + ed.doc + "'");
        check(ed.doc.toString().equals("Hello World"), "redo restores");

        ed.run(new Insert(ed.doc, 11, "!"));
        System.out.println("new command      : '" + ed.doc + "'  (redo history cleared: redo() = " + ed.redo() + ")");
        while (ed.undo()) { }
        System.out.println("undo everything  : '" + ed.doc + "'");
        check(ed.doc.length() == 0, "back to empty");
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
