package com.handbook.lld.logging;

import java.time.Clock;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Logging framework LLD. Patterns: Chain of Responsibility (each appender handles the record if its
 * level allows, then passes it on), Strategy (formatter), and a hierarchy of named loggers
 * ("com.shop.payment" inherits settings from "com.shop" unless overridden).
 */
public final class MiniLogger {

    public enum Level { DEBUG, INFO, WARN, ERROR }

    public record LogRecord(long epochMillis, Level level, String logger, String message) {
    }

    /** Strategy for turning a record into text. */
    public interface Formatter {
        String format(LogRecord r);
    }

    /** A link in the chain. */
    public static final class Appender {
        private final String name;
        private final Level threshold;
        private final Formatter formatter;
        private final Consumer<String> sink;
        private Appender next;

        public Appender(String name, Level threshold, Formatter formatter, Consumer<String> sink) {
            this.name = name;
            this.threshold = threshold;
            this.formatter = formatter;
            this.sink = sink;
        }

        public Appender then(Appender next) {
            this.next = next;
            return next;
        }

        void handle(LogRecord r) {
            if (r.level().compareTo(threshold) >= 0) {
                sink.accept("[" + name + "] " + formatter.format(r));
            }
            if (next != null) {
                next.handle(r);
            }
        }
    }

    private final Clock clock;
    private final Appender chain;
    private final Map<String, Level> levels = new TreeMap<>();

    public MiniLogger(Clock clock, Appender chain, Level rootLevel) {
        this.clock = clock;
        this.chain = chain;
        levels.put("", rootLevel);
    }

    public void setLevel(String loggerName, Level level) {
        levels.put(loggerName, level);
    }

    /** The most specific configured prefix wins: com.shop.payment → com.shop → root. */
    Level effectiveLevel(String loggerName) {
        String name = loggerName;
        while (true) {
            Level l = levels.get(name);
            if (l != null) {
                return l;
            }
            int dot = name.lastIndexOf('.');
            name = dot < 0 ? "" : name.substring(0, dot);
        }
    }

    public void log(String loggerName, Level level, String message) {
        if (level.compareTo(effectiveLevel(loggerName)) < 0) {
            return; // cheap early exit: no record, no formatting
        }
        chain.handle(new LogRecord(clock.millis(), level, loggerName, message));
    }
}
