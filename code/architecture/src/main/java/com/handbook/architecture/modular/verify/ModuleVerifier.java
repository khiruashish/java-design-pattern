package com.handbook.architecture.modular.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Like Spring Modulith's ApplicationModules.verify(): compares the dependencies a module DECLARES
 * with the ones found in the code (from bytecode analysis), and flags access to another module's
 * internal packages.
 */
public final class ModuleVerifier {

    /** A type reference found in the code: from module + type, to module + package. */
    public record Reference(String fromModule, String fromType, String toModule, String toPackage) {
    }

    private final Map<String, Set<String>> declared;

    public ModuleVerifier(Map<String, Set<String>> declaredDependencies) {
        this.declared = declaredDependencies;
    }

    public List<String> verify(List<Reference> references) {
        List<String> problems = new ArrayList<>();
        for (Reference r : references) {
            if (r.fromModule().equals(r.toModule())) {
                continue;
            }
            if (!declared.getOrDefault(r.fromModule(), Set.of()).contains(r.toModule())) {
                problems.add(r.fromType() + ": undeclared dependency " + r.fromModule() + " -> " + r.toModule());
            } else if (r.toPackage().contains(".internal")) {
                problems.add(r.fromType() + ": uses internal package " + r.toPackage());
            }
        }
        return problems;
    }
}
