package com.handbook.testing.contract.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Message contracts: can every existing consumer still read what the new producer schema emits? */
public final class SchemaCompatibility {

    public record Field(String type, boolean required) { }

    /** What a consumer actually reads from the event (its contract). */
    public record ConsumerNeeds(String consumer, Map<String, String> fields) { }

    public static List<String> check(Map<String, Field> producerSchema, List<ConsumerNeeds> consumers) {
        List<String> problems = new ArrayList<>();
        for (ConsumerNeeds c : consumers) {
            new java.util.TreeMap<>(c.fields()).forEach((name, type) -> {
                Field f = producerSchema.get(name);
                if (f == null) {
                    problems.add(c.consumer() + " reads '" + name + "' which no longer exists"); // (1)!
                } else if (!f.type().equals(type)) {
                    problems.add(c.consumer() + " expects '" + name + "' as " + type + " but it is " + f.type()); // (2)!
                } else if (!f.required()) {
                    problems.add(c.consumer() + " relies on '" + name + "' which is now optional");
                }
            });
        }
        return problems;
    }
}
