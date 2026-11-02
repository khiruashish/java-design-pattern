import java.util.*;

/** Problem: process API requests through auth -> rate limit -> validation -> handler; any step may stop the chain. */
public class ChainOfResponsibility {

    record Request(String user, String token, String body) { }
    record Response(int status, String message) { }

    /** A handler either answers (short-circuits) or delegates to the next one. */
    interface Handler {
        Response handle(Request r, Chain next);
    }

    /** Builds the chain from a list; adding or reordering steps never touches the steps themselves. */
    static final class Chain {
        private final List<Handler> handlers;
        private final int index;
        private final Handler terminal;

        Chain(List<Handler> handlers, Handler terminal) { this(handlers, 0, terminal); }
        private Chain(List<Handler> handlers, int index, Handler terminal) { this.handlers = handlers; this.index = index; this.terminal = terminal; }

        Response proceed(Request r) {
            if (index == handlers.size()) return terminal.handle(r, null);
            return handlers.get(index).handle(r, new Chain(handlers, index + 1, terminal));
        }
    }

    static Handler auth(Set<String> validTokens) {
        return (r, next) -> validTokens.contains(r.token()) ? next.proceed(r) : new Response(401, "bad token");
    }

    static Handler rateLimit(int maxPerUser) {
        Map<String, Integer> counts = new HashMap<>();
        return (r, next) -> counts.merge(r.user(), 1, Integer::sum) > maxPerUser ? new Response(429, "slow down") : next.proceed(r);
    }

    static Handler validate() {
        return (r, next) -> r.body() == null || r.body().isBlank() ? new Response(400, "empty body") : next.proceed(r);
    }

    static Handler timing(List<String> log) {   // a handler can also act AFTER the rest of the chain
        return (r, next) -> { Response resp = next.proceed(r); log.add(r.user() + " -> " + resp.status()); return resp; };
    }

    public static void main(String[] args) {
        List<String> audit = new ArrayList<>();
        Chain chain = new Chain(
                List.of(timing(audit), auth(Set.of("t-ana", "t-bob")), rateLimit(2), validate()),
                (r, none) -> new Response(201, "created for " + r.user()));

        List<Request> requests = List.of(
                new Request("ana", "t-ana", "{order}"),
                new Request("eve", "forged", "{order}"),
                new Request("bob", "t-bob", " "),
                new Request("ana", "t-ana", "{order 2}"),
                new Request("ana", "t-ana", "{order 3}"));
        List<Integer> statuses = new ArrayList<>();
        for (Request r : requests) {
            Response resp = chain.proceed(r);
            statuses.add(resp.status());
            System.out.printf("%-4s -> %d %s%n", r.user(), resp.status(), resp.message());
        }
        System.out.println("audit (post-processing): " + audit);
        check(statuses.equals(List.of(201, 401, 400, 201, 429)), "each handler short-circuits correctly");
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
