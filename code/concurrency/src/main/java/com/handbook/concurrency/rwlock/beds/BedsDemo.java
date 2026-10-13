package com.handbook.concurrency.rwlock.beds;

public class BedsDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Hospital: Bed Board ===");
        BedBoard board = new BedBoard();
        System.out.println(board.admitAndSnapshot("ICU-1", "P-2042"));
        System.out.println(board.admitAndSnapshot("ICU-2", "P-3001"));
        System.out.println("Upgrade read lock -> write lock succeeded? " + board.tryUpgradeWhileReading()
                + " (with lock() instead of tryLock() this thread would wait forever)");
    }
}
