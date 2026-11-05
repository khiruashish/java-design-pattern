package com.handbook.behavioral.interpreter.promo;

import java.util.Map;

/** Abstract syntax tree for promo rules. Each node interprets itself against a cart context. */
public sealed interface Rule {

    boolean matches(Map<String, Object> cart); // (1)!

    record And(Rule left, Rule right) implements Rule {
        public boolean matches(Map<String, Object> c) { return left.matches(c) && right.matches(c); }
        public String toString() { return "(" + left + " AND " + right + ")"; }
    }

    record Or(Rule left, Rule right) implements Rule {
        public boolean matches(Map<String, Object> c) { return left.matches(c) || right.matches(c); }
        public String toString() { return "(" + left + " OR " + right + ")"; }
    }

    record Not(Rule inner) implements Rule {
        public boolean matches(Map<String, Object> c) { return !inner.matches(c); }
        public String toString() { return "NOT " + inner; }
    }

    record Compare(String field, String op, Object value) implements Rule { // (2)!
        public boolean matches(Map<String, Object> c) {
            Object actual = c.get(field);
            if (actual == null) {
                return false;
            }
            if (value instanceof Double expected) {
                double a = ((Number) actual).doubleValue();
                return switch (op) {
                    case ">" -> a > expected;
                    case ">=" -> a >= expected;
                    case "<" -> a < expected;
                    case "<=" -> a <= expected;
                    case "==" -> a == expected;
                    default -> throw new IllegalStateException(op);
                };
            }
            return switch (op) {
                case "==" -> actual.toString().equals(value);
                case "!=" -> !actual.toString().equals(value);
                default -> throw new IllegalArgumentException("Operator " + op + " needs a number");
            };
        }

        public String toString() { return field + " " + op + " " + (value instanceof String ? "'" + value + "'" : value); }
    }
}
