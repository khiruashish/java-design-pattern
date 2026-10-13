package com.handbook.architecture.clean.crosscutting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Coding challenge: add transactions, logging and authorization to every use case WITHOUT putting
 * framework code in the interactors. Each concern is a decorator around a generic UseCase interface,
 * applied in the composition root (the outermost circle).
 */
public final class UseCaseDecorators {

    private UseCaseDecorators() {
    }

    @FunctionalInterface
    public interface UseCase<I, O> {
        O execute(I input);
    }

    /** Stands in for a database transaction manager. */
    public static final class TxManager {
        public final List<String> log = new ArrayList<>();

        void begin() {
            log.add("BEGIN");
        }

        void commit() {
            log.add("COMMIT");
        }

        void rollback() {
            log.add("ROLLBACK");
        }
    }

    public static <I, O> UseCase<I, O> transactional(UseCase<I, O> inner, TxManager tx) {
        return input -> {
            tx.begin();
            try {
                O out = inner.execute(input);
                tx.commit();
                return out;
            } catch (RuntimeException e) {
                tx.rollback();
                throw e;
            }
        };
    }

    public static <I, O> UseCase<I, O> logged(String name, UseCase<I, O> inner) {
        return input -> {
            System.out.println("    -> " + name + "(" + input + ")");
            O out = inner.execute(input);
            System.out.println("    <- " + name + " = " + out);
            return out;
        };
    }

    public static <I, O> UseCase<I, O> requiresRole(String role, Supplier<String> currentRole,
                                                   UseCase<I, O> inner) {
        return input -> {
            if (!role.equals(currentRole.get())) {
                throw new SecurityException("role " + role + " required");
            }
            return inner.execute(input);
        };
    }
}
