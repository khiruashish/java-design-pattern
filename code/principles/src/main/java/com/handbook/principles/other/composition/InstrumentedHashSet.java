package com.handbook.principles.other.composition;

import java.util.Collection;
import java.util.HashSet;

/**
 * BROKEN by inheritance (Effective Java, Item 18): HashSet.addAll() internally calls add(),
 * so every element added through addAll() is counted twice. The subclass depends on a
 * self-use implementation detail of its parent: the "fragile base class" problem.
 */
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int addCount() { return addCount; }
}
