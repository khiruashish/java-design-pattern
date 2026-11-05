package com.handbook.microservices.leaderelection.fencing;

import java.util.ArrayList;
import java.util.List;

/** The storage side of fencing: it rejects writes carrying a token older than one it has already seen. */
public final class FencedStorage {

    private long highestToken;
    private final List<String> written = new ArrayList<>();

    public synchronized boolean write(long fencingToken, String line) {
        if (fencingToken < highestToken) { // (1)!
            return false;
        }
        highestToken = fencingToken;
        written.add(line);
        return true;
    }

    public synchronized List<String> written() {
        return List.copyOf(written);
    }

    /** The lock service: every grant gets a strictly increasing token. */
    public static final class LockService {
        private long nextToken = 33;

        public synchronized long grant(String holder) { // (2)!
            return ++nextToken;
        }
    }
}
