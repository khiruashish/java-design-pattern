package com.handbook.behavioral.command;

import com.handbook.behavioral.command.banking.CommandProcessor;
import com.handbook.behavioral.command.banking.Ledger;
import com.handbook.behavioral.command.banking.PaymentCommand.Transfer;
import com.handbook.behavioral.command.cart.CartCommands.*;
import com.handbook.behavioral.command.editor.Document;
import com.handbook.behavioral.command.editor.EditCommand.Delete;
import com.handbook.behavioral.command.editor.EditCommand.Insert;
import com.handbook.behavioral.command.editor.Editor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandTest {

    @Test
    void undoRestoresEveryPreviousState() {
        Document doc = new Document();
        Editor editor = new Editor();
        editor.run(new Insert(doc, 0, "abc"));
        editor.run(new Delete(doc, 1, 1));
        assertThat(doc).hasToString("\"ac\"");
        editor.undo();
        assertThat(doc).hasToString("\"abc\"");
        editor.undo();
        assertThat(doc).hasToString("\"\"");
        assertThat(editor.undo()).isEqualTo("nothing to undo");
    }

    @Test
    void newEditClearsRedo() {
        Document doc = new Document();
        Editor editor = new Editor();
        editor.run(new Insert(doc, 0, "a"));
        editor.undo();
        editor.run(new Insert(doc, 0, "b"));
        assertThat(editor.redo()).isEqualTo("nothing to redo");
    }

    @Test
    void duplicateCommandsExecuteOnce() {
        Ledger ledger = new Ledger();
        CommandProcessor p = new CommandProcessor(ledger);
        p.submit(new Transfer("X", "ACC-1", "ACC-2", 100));
        p.submit(new Transfer("X", "ACC-1", "ACC-2", 100));
        p.runBatch();
        assertThat(ledger.balance("ACC-1")).isEqualTo(9_900);
    }

    @Test
    void macroUndoIsAllOrNothing() {
        Cart cart = new Cart();
        Macro m = new Macro(List.of(new AddItem(cart, "A", 1), new AddItem(cart, "B", 2)));
        m.execute();
        m.undo();
        assertThat(cart).hasToString("{}");
    }
}
