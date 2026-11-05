package com.handbook.concurrency.forkjoin.masking;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.LongAdder;

public class MaskingDemo {

    public static void main(String[] args) {
        System.out.println("=== Healthcare: Masking National IDs Before Sharing Research Data ===");
        String[] records = new String[400_000];
        for (int i = 0; i < records.length; i++) {
            records[i] = i % 4 == 0
                    ? "patient=" + i + ";dx=E11.9;note=no id recorded"
                    : "patient=" + i + ";dx=I10;id=" + String.format("%012d", 234_500_000_000L + i);
        }
        LongAdder masked = new LongAdder();
        try (ForkJoinPool pool = new ForkJoinPool(4)) {
            pool.invoke(new MaskIdentifiers(records, 0, records.length, masked));
        }
        System.out.println("Records scanned : " + records.length);
        System.out.println("IDs masked      : " + masked.sum());
        System.out.println("Sample          : " + records[1]);
        System.out.println("Untouched       : " + records[0]);
    }
}
