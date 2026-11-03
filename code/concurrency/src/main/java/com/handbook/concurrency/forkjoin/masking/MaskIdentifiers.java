package com.handbook.concurrency.forkjoin.masking;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.LongAdder;

/** RecursiveAction: no result, just work done in place on disjoint slices. */
public final class MaskIdentifiers extends RecursiveAction {

    private static final int THRESHOLD = 10_000;

    private final String[] records;
    private final int from;
    private final int to;
    private final LongAdder masked;

    public MaskIdentifiers(String[] records, int from, int to, LongAdder masked) {
        this.records = records;
        this.from = from;
        this.to = to;
        this.masked = masked;
    }

    @Override
    protected void compute() {
        if (to - from <= THRESHOLD) {
            for (int i = from; i < to; i++) {
                String r = records[i];
                String m = r.replaceAll("\\b(\\d{4})(\\d{4})(\\d{4})\\b", "XXXX-XXXX-$3"); // 12-digit national id
                if (!m.equals(r)) {
                    records[i] = m; // (1)!
                    masked.increment();
                }
            }
            return;
        }
        int mid = (from + to) >>> 1;
        invokeAll(new MaskIdentifiers(records, from, mid, masked), new MaskIdentifiers(records, mid, to, masked)); // (2)!
    }
}
