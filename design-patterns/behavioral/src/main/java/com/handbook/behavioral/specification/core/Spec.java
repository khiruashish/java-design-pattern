package com.handbook.behavioral.specification.core;

import java.util.function.Predicate;

/** A named, composable business rule. */
public interface Spec<T> {

    boolean isSatisfiedBy(T candidate);

    String describe();

    static <T> Spec<T> of(String description, Predicate<T> rule) { // (1)!
        return new Spec<>() {
            public boolean isSatisfiedBy(T t) { return rule.test(t); }
            public String describe() { return description; }
        };
    }

    default Spec<T> and(Spec<T> other) { // (2)!
        return of("(" + describe() + " AND " + other.describe() + ")",
                t -> isSatisfiedBy(t) && other.isSatisfiedBy(t));
    }

    default Spec<T> or(Spec<T> other) {
        return of("(" + describe() + " OR " + other.describe() + ")",
                t -> isSatisfiedBy(t) || other.isSatisfiedBy(t));
    }

    default Spec<T> not() {
        return of("NOT " + describe(), t -> !isSatisfiedBy(t));
    }
}
