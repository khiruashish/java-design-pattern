package com.handbook.enterprise.aggregate.banking;

import java.util.LinkedHashMap;
import java.util.Map;

/** Invariant: balance + all open holds <= credit limit. Holds are internal to the aggregate. */
public final class CardAccount {

    private final String cardId;
    private final long limitPaise;
    private long postedPaise;
    private final Map<String, Long> holds = new LinkedHashMap<>(); // authCode -> amount
    private long version;

    public CardAccount(String cardId, long limitPaise) {
        this.cardId = cardId;
        this.limitPaise = limitPaise;
    }

    public long available() {
        return limitPaise - postedPaise - holds.values().stream().mapToLong(Long::longValue).sum();
    }

    public boolean authorise(String authCode, long paise) { // (1)!
        if (paise > available()) {
            return false;
        }
        holds.put(authCode, paise);
        version++;
        return true;
    }

    public void capture(String authCode, long finalPaise) { // (2)!
        Long held = holds.remove(authCode);
        if (held == null) {
            throw new IllegalStateException("unknown or already settled auth " + authCode);
        }
        if (finalPaise > held) {
            holds.put(authCode, held);
            throw new IllegalArgumentException("capture " + finalPaise + " exceeds hold " + held);
        }
        postedPaise += finalPaise;
        version++;
    }

    public void release(String authCode) {
        if (holds.remove(authCode) != null) {
            version++;
        }
    }

    public long version() { return version; }

    @Override
    public String toString() {
        return cardId + "[posted=" + postedPaise / 100 + ", holds=" + holds.size() + ", available=" + available() / 100 + "]";
    }
}
