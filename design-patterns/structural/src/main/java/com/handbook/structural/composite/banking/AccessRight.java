package com.handbook.structural.composite.banking;

import java.util.Set;

/** Component: a single permission or a role made of permissions and other roles. */
public interface AccessRight {

    String name();

    boolean grants(String permission);

    void collect(Set<String> into);
}
