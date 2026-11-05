package com.handbook.creational.objectpool.banking;

/** Stand-in for a real JDBC connection: expensive to open, can break. */
public class DbConnection {

    private static int opened = 0;

    private final int id;
    private boolean broken;
    private boolean inTransaction;

    DbConnection() {
        this.id = ++opened;
        System.out.println("  [db] opening connection #" + id + " (TCP + TLS + auth, ~50 ms)");
    }

    public int id() { return id; }

    public String query(String sql) {
        inTransaction = sql.startsWith("BEGIN") || inTransaction;
        return "conn#" + id + " ran: " + sql;
    }

    boolean isValid() { return !broken; }

    void reset() { inTransaction = false; } // roll back anything left open

    boolean inTransaction() { return inTransaction; }

    void simulateNetworkDrop() { broken = true; }

    void close() { System.out.println("  [db] closing connection #" + id); }
}
