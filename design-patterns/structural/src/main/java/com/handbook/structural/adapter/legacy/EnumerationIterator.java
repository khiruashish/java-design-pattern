package com.handbook.structural.adapter.legacy;

import java.util.Enumeration;
import java.util.Iterator;

/** Challenge solution: let old Enumeration-based APIs work with modern Iterator code. */
public class EnumerationIterator<T> implements Iterator<T> {

    private final Enumeration<T> enumeration;

    public EnumerationIterator(Enumeration<T> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public T next() {
        return enumeration.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Enumeration does not support remove()");
    }

    /** Bonus: use it in a for-each loop. */
    public static <T> Iterable<T> iterable(Enumeration<T> e) {
        return () -> new EnumerationIterator<>(e);
    }
}
