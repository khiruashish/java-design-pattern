package com.handbook.principles.solid.ocp.after;

/** The extension point. New shapes implement this; nothing that uses Shape has to change. */
public interface Shape {
    double area();

    String name();
}
