package com.handbook.behavioral.command.editor;

import com.handbook.behavioral.command.editor.EditCommand.Delete;
import com.handbook.behavioral.command.editor.EditCommand.Insert;

public class EditorDemo {

    public static void main(String[] args) {
        System.out.println("=== Docs SaaS: Undo / Redo ===");
        Document doc = new Document();
        Editor editor = new Editor();

        editor.run(new Insert(doc, 0, "Hello world"));
        System.out.println("type           -> " + doc);
        editor.run(new Insert(doc, 5, ", dear"));
        System.out.println("insert         -> " + doc);
        editor.run(new Delete(doc, 0, 7));
        System.out.println("delete         -> " + doc);

        System.out.println(editor.undo() + " -> " + doc);
        System.out.println(editor.undo() + " -> " + doc);
        System.out.println(editor.redo() + " -> " + doc);
        editor.run(new Insert(doc, doc.length(), "!"));
        System.out.println("new edit       -> " + doc);
        System.out.println(editor.redo());
    }
}
