package com.handbook.creational.objectpool.travel;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

/** Pool with a "max uses" refresh policy, like connection max-lifetime settings. */
public class GdsSessionPool {

    private final int licences;
    private final int maxUsesPerSession;
    private final Deque<GdsSession> idle = new ArrayDeque<>();
    private int live;
    private int nextId = 1;

    public GdsSessionPool(int licences, int maxUsesPerSession) {
        this.licences = licences;
        this.maxUsesPerSession = maxUsesPerSession;
    }

    public <T> T withSession(Function<GdsSession, T> work) { // (1)!
        GdsSession s = borrow();
        try {
            return work.apply(s);
        } finally {
            giveBack(s);
        }
    }

    private synchronized GdsSession borrow() {
        if (!idle.isEmpty()) {
            return idle.pop();
        }
        if (live == licences) {
            throw new IllegalStateException("All " + licences + " GDS licences in use");
        }
        live++;
        return new GdsSession("S" + nextId++);
    }

    private synchronized void giveBack(GdsSession s) {
        if (s.uses() >= maxUsesPerSession) { // (2)!
            s.logout();
            live--;
        } else {
            idle.push(s);
        }
    }
}
