package com.handbook.testing.contract.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/** A tiny Pact-like consumer-driven contract: interactions written by the consumer, verified against the provider. */
public final class Contract {

    /** Match by TYPE or pattern, not by exact value, so provider test data can differ. */
    public sealed interface Matcher permits AnyString, AnyInteger, Regex, Exactly {
        boolean matches(Object actual);
    }

    public record AnyString() implements Matcher {
        public boolean matches(Object a) { return a instanceof String; }
        public String toString() { return "a string"; }
    }

    public record AnyInteger() implements Matcher {
        public boolean matches(Object a) { return a instanceof Integer || a instanceof Long; }
        public String toString() { return "an integer"; }
    }

    public record Regex(String pattern) implements Matcher {
        public boolean matches(Object a) { return a instanceof String s && Pattern.matches(pattern, s); }
        public String toString() { return "matching /" + pattern + "/"; }
    }

    public record Exactly(Object value) implements Matcher {
        public boolean matches(Object a) { return value.equals(a); }
        public String toString() { return "exactly " + value; }
    }

    public record Interaction(String description, String providerState, String path, int status, Map<String, Matcher> body) { }

    private final String consumer;
    private final String provider;
    private final List<Interaction> interactions = new ArrayList<>();

    public Contract(String consumer, String provider) {
        this.consumer = consumer;
        this.provider = provider;
    }

    public Contract uponReceiving(Interaction i) {
        interactions.add(i);
        return this;
    }

    public record Response(int status, Map<String, Object> body) { }

    /** Provider-side verification: set up each provider state, replay the request, check the response. */
    public List<String> verify(Consumer<String> setUpState, Function<String, Response> providerApp) {
        List<String> report = new ArrayList<>();
        for (Interaction i : interactions) {
            setUpState.accept(i.providerState()); // (1)!
            Response r = providerApp.apply(i.path());
            List<String> problems = new ArrayList<>();
            if (r.status() != i.status()) {
                problems.add("status " + r.status() + " != " + i.status());
            }
            new java.util.TreeMap<>(i.body()).forEach((field, matcher) -> { // (2)!
                Object actual = r.body().get(field);
                if (actual == null) {
                    problems.add("missing field '" + field + "'");
                } else if (!matcher.matches(actual)) {
                    problems.add("'" + field + "' = " + actual + " is not " + matcher);
                }
            });
            report.add((problems.isEmpty() ? "PASS " : "FAIL ") + consumer + " -> " + provider + ": " + i.description()
                    + (problems.isEmpty() ? "" : " " + problems));
        }
        return report;
    }
}
