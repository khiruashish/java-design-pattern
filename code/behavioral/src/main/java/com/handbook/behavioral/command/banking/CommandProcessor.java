package com.handbook.behavioral.command.banking;

import com.handbook.behavioral.command.banking.PaymentCommand.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Invoker: queues commands, executes them later, audits, de-duplicates and dead-letters. */
public class CommandProcessor {

    private final Ledger ledger;
    private final Queue<PaymentCommand> queue = new ArrayDeque<>();
    private final Set<String> processed = new HashSet<>();
    private final List<String> deadLetters = new ArrayList<>();

    public CommandProcessor(Ledger ledger) {
        this.ledger = ledger;
    }

    public void submit(PaymentCommand command) {
        queue.add(command); // (1)!
    }

    public void runBatch() {
        while (!queue.isEmpty()) {
            PaymentCommand c = queue.poll();
            if (!processed.add(c.id())) { // (2)!
                System.out.println("  skip duplicate " + c.id());
                continue;
            }
            try {
                execute(c);
                System.out.println("  done " + c);
            } catch (IllegalStateException e) {
                deadLetters.add(c.id() + ": " + e.getMessage());
                System.out.println("  FAILED " + c.id() + " -> dead-letter queue");
            }
        }
    }

    private void execute(PaymentCommand c) {
        switch (c) { // (3)!
            case Transfer t -> { ledger.debit(t.from(), t.amountInr()); ledger.credit(t.to(), t.amountInr()); }
            case PayBill b -> { ledger.debit(b.from(), b.amountInr()); ledger.credit("BILLER:" + b.biller(), b.amountInr()); }
            case MobileRecharge r -> { ledger.debit(r.from(), r.amountInr()); ledger.credit("TELCO:" + r.phone(), r.amountInr()); }
        }
    }

    public List<String> deadLetters() {
        return deadLetters;
    }
}
