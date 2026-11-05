package com.handbook.behavioral.iterator.statement;

public class StatementDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Statement with Lazy Pagination ===");
        StatementApi api = new StatementApi();
        Transactions txns = new Transactions(api, "SB-1001");

        System.out.println("Mini statement (first 4 only):");
        txns.stream().limit(4).forEach(t -> System.out.println("  #" + t.no() + " " + t.description() + " " + t.amountInr()));
        System.out.println("API calls so far: " + api.calls());

        System.out.println("Full statement total:");
        long total = 0;
        for (StatementApi.Txn t : txns) { // the same Iterable works in a for-each loop
            total += t.amountInr();
        }
        System.out.println("  net INR " + total + ", API calls so far: " + api.calls());
    }
}
