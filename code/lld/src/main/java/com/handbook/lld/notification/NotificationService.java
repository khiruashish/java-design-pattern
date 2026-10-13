package com.handbook.lld.notification;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Notification system LLD. Patterns: Strategy (one sender per channel), Factory/registry (channel →
 * sender), Template Method (every message renders the same way with channel-specific formatting),
 * Decorator (retry around any sender), and user preferences as data.
 */
public final class NotificationService {

    public enum Channel { EMAIL, SMS, PUSH }

    public record User(String id, String name, String email, String phone, Set<Channel> optedIn) {
    }

    /** Strategy. */
    public interface Sender {
        void send(User user, String text);
    }

    /** Template Method: fixed steps, channel-specific formatting hook. */
    public abstract static class MessageTemplate {
        public final String render(User user, Map<String, String> data) {
            return format(greeting(user) + body(data)); // the skeleton never changes
        }

        protected String greeting(User user) {
            return "Hi " + user.name() + ", ";
        }

        protected abstract String body(Map<String, String> data);

        protected String format(String text) {
            return text;
        }
    }

    /** Decorator: retries any sender a few times on failure. */
    public static Sender withRetry(Sender inner, int attempts, List<String> log) {
        return (user, text) -> {
            for (int i = 1; ; i++) {
                try {
                    inner.send(user, text);
                    return;
                } catch (RuntimeException e) {
                    log.add("retry " + i + " after: " + e.getMessage());
                    if (i == attempts) {
                        throw e;
                    }
                }
            }
        };
    }

    private final Map<Channel, Sender> senders = new EnumMap<>(Channel.class);
    private final List<String> failures = new ArrayList<>();

    public NotificationService register(Channel channel, Sender sender) {
        senders.put(channel, sender);
        return this;
    }

    public void notify(User user, MessageTemplate template, Map<String, String> data) {
        String text = template.render(user, data);
        for (Channel c : Channel.values()) {
            if (!user.optedIn().contains(c)) {
                continue; // respect preferences
            }
            try {
                senders.get(c).send(user, c == Channel.SMS && text.length() > 60 ? text.substring(0, 57) + "..." : text);
            } catch (RuntimeException e) {
                failures.add(c + " to " + user.id() + " failed: " + e.getMessage()); // one channel failing doesn't stop others
            }
        }
    }

    public List<String> failures() {
        return List.copyOf(failures);
    }
}
