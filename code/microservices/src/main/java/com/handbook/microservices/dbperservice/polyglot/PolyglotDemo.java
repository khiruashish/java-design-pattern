package com.handbook.microservices.dbperservice.polyglot;

import com.handbook.microservices.dbperservice.polyglot.PolyglotStores.SearchIndex;
import com.handbook.microservices.dbperservice.polyglot.PolyglotStores.SessionStore;
import com.handbook.microservices.support.Db;
import com.handbook.microservices.support.FakeClock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

public class PolyglotDemo {

    public static void main(String[] args) throws SQLException {
        System.out.println("=== E-commerce: each service picks the store that fits ===");

        FakeClock clock = FakeClock.at("2026-10-13T10:00:00Z");
        SessionStore sessions = new SessionStore(clock);
        sessions.put("sess-91", "CUST-42", Duration.ofMinutes(30));
        System.out.println("session-service (key-value + TTL): sess-91 -> " + sessions.get("sess-91"));
        clock.advance(Duration.ofMinutes(31));
        System.out.println("                31 minutes later : sess-91 -> " + sessions.get("sess-91"));

        SearchIndex search = new SearchIndex();
        search.index("P1", "Organic Alphonso Mangoes 1 kg");
        search.index("P2", "Mango Pickle 500 g");
        search.index("P3", "Organic Basmati Rice 5 kg");
        System.out.println("search-service (inverted index)  : 'organic' -> " + search.search("organic"));
        System.out.println("                                   'organic mangoes' -> " + search.search("organic mangoes"));

        DataSource ledger = Db.inMemory("ledger_db",
                "CREATE TABLE entries (id BIGINT AUTO_INCREMENT, account VARCHAR, amount BIGINT CHECK (amount <> 0))");
        try (Connection c = ledger.getConnection(); Statement s = c.createStatement()) {
            c.setAutoCommit(false);
            s.executeUpdate("INSERT INTO entries (account, amount) VALUES ('customer:CUST-42', -79800)");
            s.executeUpdate("INSERT INTO entries (account, amount) VALUES ('merchant:FRESHCO', 79800)");
            c.commit(); // both legs of the double entry, atomically
        }
        System.out.println("payment-service (relational, ACID): ledger sums to "
                + Db.count(ledger, "SELECT SUM(amount) FROM entries") + " across "
                + Db.count(ledger, "SELECT COUNT(*) FROM entries") + " entries");
    }
}
