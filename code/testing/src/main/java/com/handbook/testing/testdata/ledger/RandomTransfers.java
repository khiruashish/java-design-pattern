package com.handbook.testing.testdata.ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Generated test data: many random-but-valid transfers from a SEED, so any failure can be replayed exactly. */
public final class RandomTransfers {

    public record Transfer(String from, String to, long paise) { }

    public static List<Transfer> generate(long seed, int count, List<String> accounts) {
        Random r = new Random(seed); // (1)!
        List<Transfer> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String from = accounts.get(r.nextInt(accounts.size()));
            String to;
            do {
                to = accounts.get(r.nextInt(accounts.size()));
            } while (to.equals(from)); // (2)!
            out.add(new Transfer(from, to, 100 + r.nextInt(1_000_000)));
        }
        return out;
    }

    private RandomTransfers() {
    }
}
