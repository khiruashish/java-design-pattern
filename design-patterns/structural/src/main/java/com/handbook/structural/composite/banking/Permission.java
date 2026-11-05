package com.handbook.structural.composite.banking;

import java.util.Set;

/** Leaf. */
public record Permission(String name) implements AccessRight {

    public boolean grants(String permission) {
        return name.equals(permission);
    }

    public void collect(Set<String> into) {
        into.add(name);
    }
}
