package com.handbook.behavioral.iterator.statement;

import java.util.ArrayList;
import java.util.List;

/** Remote API that returns transactions one page at a time (cursor-based pagination). */
public class StatementApi {

    public record Txn(int no, String description, long amountInr) {
    }

    public record Page(List<Txn> items, String nextCursor) {
    }

    private static final int PAGE_SIZE = 3;
    private static final int TOTAL = 8;
    private int calls;

    public Page fetch(String account, String cursor) {
        calls++;
        int start = cursor == null ? 0 : Integer.parseInt(cursor);
        System.out.println("  [api] GET /accounts/" + account + "/txns?cursor=" + cursor);
        List<Txn> items = new ArrayList<>();
        for (int i = start; i < Math.min(start + PAGE_SIZE, TOTAL); i++) {
            items.add(new Txn(i + 1, i % 2 == 0 ? "UPI to grocery" : "Salary credit", i % 2 == 0 ? -450 - i : 85_000));
        }
        String next = start + PAGE_SIZE < TOTAL ? String.valueOf(start + PAGE_SIZE) : null;
        return new Page(items, next);
    }

    public int calls() {
        return calls;
    }
}
