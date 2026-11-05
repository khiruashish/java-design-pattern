package com.handbook.structural.composite.banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Composite. Roles can include roles, e.g. BranchManager includes Teller. */
public final class Role implements AccessRight {

    private final String name;
    private final List<AccessRight> rights = new ArrayList<>();

    public Role(String name) {
        this.name = name;
    }

    public Role include(AccessRight right) {
        if (right == this || (right instanceof Role r && r.contains(this))) { // (1)!
            throw new IllegalArgumentException("Cycle: " + right.name() + " already includes " + name);
        }
        rights.add(right);
        return this;
    }

    private boolean contains(Role target) {
        return rights.stream().anyMatch(r -> r == target || (r instanceof Role role && role.contains(target)));
    }

    public String name() {
        return name;
    }

    public boolean grants(String permission) {
        return rights.stream().anyMatch(r -> r.grants(permission)); // (2)!
    }

    public void collect(Set<String> into) {
        rights.forEach(r -> r.collect(into));
    }
}
