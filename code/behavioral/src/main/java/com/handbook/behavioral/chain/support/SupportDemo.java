package com.handbook.behavioral.chain.support;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** Challenge solution: ticket routing as a list of functions; first non-empty answer wins. */
public class SupportDemo {

    record Ticket(String id, String text, boolean vip) {
    }

    static Optional<String> route(Ticket t, List<Function<Ticket, Optional<String>>> handlers) {
        return handlers.stream().map(h -> h.apply(t)).flatMap(Optional::stream).findFirst();
    }

    public static void main(String[] args) {
        List<Function<Ticket, Optional<String>>> handlers = List.of(
                t -> t.text().contains("password") ? Optional.of("Bot: sent reset link") : Optional.empty(),
                t -> t.vip() ? Optional.of("VIP desk: assigned to senior agent") : Optional.empty(),
                t -> t.text().contains("refund") ? Optional.of("L1 agent: refund initiated") : Optional.empty(),
                t -> t.text().contains("crash") ? Optional.of("Engineering: bug ticket created") : Optional.empty());

        List<Ticket> tickets = List.of(
                new Ticket("T1", "forgot password", false),
                new Ticket("T2", "app crash on checkout", true),
                new Ticket("T3", "want refund for damaged item", false),
                new Ticket("T4", "app crash on login", false),
                new Ticket("T5", "do you sell gift cards?", false));

        for (Ticket t : tickets) {
            System.out.println(t.id() + " -> " + route(t, handlers).orElse("Unhandled: queued for L2 triage"));
        }
    }
}
