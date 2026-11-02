package com.handbook.principles.oop.inheritance;

/**
 * Inheritance: shared state and behaviour live in the base class; subclasses specialise.
 * The template method describe() is final so subclasses can't break the format, only fill in the hook.
 */
public abstract class Vehicle {
    private final String plate;
    private final int wheels;

    protected Vehicle(String plate, int wheels) {
        this.plate = plate;
        this.wheels = wheels;
    }

    /** Hook that subclasses must provide. */
    protected abstract long tollCents();

    public final String describe() {
        return getClass().getSimpleName() + " " + plate + " (" + wheels + " wheels) toll=" + tollCents();
    }
}
