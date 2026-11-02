package com.handbook.principles.other.kiss;

import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/** KISS: the same requirement ("is this year a leap year? what is the max?"), solved clever vs simple. */
public final class KissExamples {
    private KissExamples() { }

    // ---- too clever: a generic "strategy factory" for a one-line rule
    public static boolean isLeapClever(int year) {
        Supplier<Function<Integer, BinaryOperator<Boolean>>> factory =
                () -> divisor -> (acc, unused) -> acc && year % divisor == 0;
        boolean by4 = factory.get().apply(4).apply(true, true);
        boolean by100 = factory.get().apply(100).apply(true, true);
        boolean by400 = factory.get().apply(400).apply(true, true);
        return by4 && (!by100 || by400);
    }

    // ---- simple: reads like the rule itself
    public static boolean isLeapSimple(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    // ---- hand-rolled loop vs the library: the library is simpler AND already tested
    public static int maxHandRolled(List<Integer> values) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) > max) max = values.get(i);
        }
        return max;
    }

    public static int maxSimple(List<Integer> values) {
        return Collections.max(values);
    }
}
