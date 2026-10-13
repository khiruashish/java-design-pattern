package com.handbook.microservices.support;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Renders maps, lists, strings, numbers, booleans and null as compact JSON (insertion order kept). */
public final class Json {

    private Json() {
    }

    /** An ordered map from alternating keys and values: {@code obj("id", 1, "name", "x")}. */
    public static Map<String, Object> obj(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }

    public static String of(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String s) {
            return "\"" + s.replace("\"", "\\\"") + "\"";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map<?, ?> m) {
            return m.entrySet().stream()
                    .map(e -> of(String.valueOf(e.getKey())) + ":" + of(e.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));
        }
        if (value instanceof Collection<?> c) {
            return c.stream().map(Json::of).collect(Collectors.joining(",", "[", "]"));
        }
        return of(value.toString());
    }
}
