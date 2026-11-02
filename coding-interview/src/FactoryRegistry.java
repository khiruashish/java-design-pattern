import java.util.*;
import java.util.function.Supplier;

/** Problem: create notification senders by channel name without a growing switch; allow new channels at runtime. */
public class FactoryRegistry {

    interface Notifier { String send(String to, String msg); }

    static final class EmailNotifier implements Notifier { public String send(String to, String msg) { return "EMAIL " + to + ": " + msg; } }
    static final class SmsNotifier implements Notifier { public String send(String to, String msg) { return "SMS " + to + ": " + msg; } }

    /** The factory: a registry of constructors. Adding a channel = one register() call, no edit here (OCP). */
    static final class NotifierFactory {
        private final Map<String, Supplier<? extends Notifier>> registry = new HashMap<>();

        NotifierFactory register(String channel, Supplier<? extends Notifier> creator) {
            if (registry.putIfAbsent(channel.toLowerCase(Locale.ROOT), creator) != null) {
                throw new IllegalArgumentException("channel already registered: " + channel);
            }
            return this;
        }

        Notifier create(String channel) {
            Supplier<? extends Notifier> creator = registry.get(channel.toLowerCase(Locale.ROOT));
            if (creator == null) throw new IllegalArgumentException("unknown channel '" + channel + "', known: " + new TreeSet<>(registry.keySet()));
            return creator.get();
        }
    }

    /** For contrast: the switch version you are asked to refactor. Every new channel edits this method. */
    static Notifier createWithSwitch(String channel) {
        return switch (channel) {
            case "email" -> new EmailNotifier();
            case "sms" -> new SmsNotifier();
            default -> throw new IllegalArgumentException(channel);
        };
    }

    public static void main(String[] args) {
        NotifierFactory factory = new NotifierFactory()
                .register("email", EmailNotifier::new)
                .register("sms", SmsNotifier::new);

        System.out.println(factory.create("EMAIL").send("ana@example.com", "order shipped"));
        System.out.println(factory.create("sms").send("+15550100", "order shipped"));

        factory.register("push", () -> (to, msg) -> "PUSH " + to + ": " + msg);   // new channel, no factory edit
        System.out.println(factory.create("push").send("device-42", "order shipped"));
        check(factory.create("push").send("d", "m").startsWith("PUSH"), "runtime extension");
        check(factory.create("email") != factory.create("email"), "a new instance per call");
        check(createWithSwitch("sms") instanceof SmsNotifier, "switch version");

        try { factory.create("fax"); check(false, "unknown"); }
        catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
