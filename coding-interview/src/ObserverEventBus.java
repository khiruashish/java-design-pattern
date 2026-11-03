import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Problem: a type-safe in-process event bus: subscribe by event type, unsubscribe, isolate failing listeners. */
public class ObserverEventBus {

    sealed interface Event permits OrderPlaced, OrderCancelled { }
    record OrderPlaced(String orderId, long cents) implements Event { }
    record OrderCancelled(String orderId, String reason) implements Event { }

    static final class EventBus {
        private final Map<Class<? extends Event>, List<Consumer<? super Event>>> listeners = new HashMap<>();
        private final List<String> errors = new ArrayList<>();

        /** Returns a handle so the caller can unsubscribe (avoids the lapsed-listener memory leak). */
        synchronized <E extends Event> AutoCloseable subscribe(Class<E> type, Consumer<? super E> listener) {
            Consumer<? super Event> wrapper = event -> listener.accept(type.cast(event));
            listeners.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>()).add(wrapper);
            return () -> { synchronized (this) { listeners.get(type).remove(wrapper); } };
        }

        void publish(Event event) {
            List<Consumer<? super Event>> targets;
            synchronized (this) { targets = List.copyOf(listeners.getOrDefault(event.getClass(), List.of())); }
            for (Consumer<? super Event> l : targets) {
                try {
                    l.accept(event);
                } catch (RuntimeException e) {        // one bad listener must not stop the others
                    errors.add(event.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        }

        List<String> errors() { return List.copyOf(errors); }
    }

    public static void main(String[] args) throws Exception {
        EventBus bus = new EventBus();
        List<String> log = new ArrayList<>();

        AutoCloseable email = bus.subscribe(OrderPlaced.class, e -> log.add("email: thanks for " + e.orderId()));
        bus.subscribe(OrderPlaced.class, e -> log.add("analytics: +" + e.cents()));
        bus.subscribe(OrderPlaced.class, e -> { throw new IllegalStateException("loyalty service down"); });
        bus.subscribe(OrderCancelled.class, e -> log.add("refund: " + e.orderId() + " (" + e.reason() + ")"));

        bus.publish(new OrderPlaced("A-1", 4_200));
        email.close();                                   // unsubscribe
        bus.publish(new OrderPlaced("A-2", 1_000));
        bus.publish(new OrderCancelled("A-1", "changed mind"));

        log.forEach(System.out::println);
        System.out.println("isolated errors: " + bus.errors());
        check(log.size() == 4, "email only for A-1, analytics for both, one refund");
        check(log.stream().noneMatch(s -> s.equals("email: thanks for A-2")), "unsubscribed listener not called");
        check(bus.errors().size() == 2, "failing listener isolated on both publishes");
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
