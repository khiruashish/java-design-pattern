package com.handbook.capstone.ordering;

import com.handbook.capstone.shared.DomainEvents;
import com.handbook.capstone.shared.DomainEvents.Event;
import com.handbook.capstone.shared.DomainEvents.OrderPlaced;
import com.handbook.capstone.shared.DomainEvents.OrderStatusChanged;
import com.handbook.capstone.shared.Money;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ordering's persistence (real H2 database). [Transactional Outbox]: every order change and the
 * event describing it are written in ONE transaction; {@link #relay} publishes them afterwards.
 * Checkout idempotency keys are a UNIQUE column, so a retried request can't create a second order.
 */
public final class OrderStore {

    public record OrderRow(String id, String customerId, Money total, OrderState state, String idempotencyKey) {
    }

    private static final AtomicInteger DB_COUNTER = new AtomicInteger();

    private final JdbcDataSource db = new JdbcDataSource();

    public OrderStore() {
        db.setURL("jdbc:h2:mem:freshcart_" + DB_COUNTER.incrementAndGet() + ";DB_CLOSE_DELAY=-1");
        exec("CREATE TABLE orders (id VARCHAR PRIMARY KEY, customer VARCHAR, total_paise BIGINT, state VARCHAR,"
                + " idempotency_key VARCHAR UNIQUE)");
        exec("CREATE TABLE outbox (seq BIGINT AUTO_INCREMENT PRIMARY KEY, event_id VARCHAR, type VARCHAR,"
                + " order_id VARCHAR, customer VARCHAR, total_paise BIGINT, state VARCHAR, published BOOLEAN DEFAULT FALSE)");
    }

    public void insert(OrderRow o, String eventId) {
        inTransaction(c -> {
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO orders VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, o.id());
                ps.setString(2, o.customerId());
                ps.setLong(3, o.total().paise());
                ps.setString(4, o.state().name());
                ps.setString(5, o.idempotencyKey());
                ps.executeUpdate();
            }
            outbox(c, eventId, "OrderPlaced", o.id(), o.customerId(), o.total().paise(), o.state().name());
        });
    }

    public void updateState(OrderRow o, OrderState next, String eventId) {
        inTransaction(c -> {
            try (PreparedStatement ps = c.prepareStatement("UPDATE orders SET state = ? WHERE id = ? AND state = ?")) {
                ps.setString(1, next.name());
                ps.setString(2, o.id());
                ps.setString(3, o.state().name()); // optimistic check: nobody changed it meanwhile
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("concurrent update of " + o.id());
                }
            }
            outbox(c, eventId, "OrderStatusChanged", o.id(), o.customerId(), o.total().paise(), next.name());
        });
    }

    public Optional<OrderRow> byId(String id) {
        return query("SELECT * FROM orders WHERE id = ?", id);
    }

    public Optional<OrderRow> byIdempotencyKey(String key) {
        return query("SELECT * FROM orders WHERE idempotency_key = ?", key);
    }

    /** Publishes unpublished outbox rows in order (at-least-once) and marks them. */
    public int relay(DomainEvents events) {
        List<Event> pending = new ArrayList<>();
        List<Long> seqs = new ArrayList<>();
        try (Connection c = db.getConnection(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM outbox WHERE published = FALSE ORDER BY seq")) {
            while (rs.next()) {
                seqs.add(rs.getLong("seq"));
                Money total = new Money(rs.getLong("total_paise"));
                pending.add(rs.getString("type").equals("OrderPlaced")
                        ? new OrderPlaced(rs.getString("event_id"), rs.getString("order_id"), rs.getString("customer"), total)
                        : new OrderStatusChanged(rs.getString("event_id"), rs.getString("order_id"), rs.getString("customer"),
                        rs.getString("state")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        for (int i = 0; i < pending.size(); i++) {
            events.publish(pending.get(i));
            long seq = seqs.get(i);
            inTransaction(c -> {
                try (PreparedStatement ps = c.prepareStatement("UPDATE outbox SET published = TRUE WHERE seq = ?")) {
                    ps.setLong(1, seq);
                    ps.executeUpdate();
                }
            });
        }
        return pending.size();
    }

    /** Test hook: mark everything unpublished again, as if the relay crashed before marking. */
    public void simulateRelayCrashBeforeMarking() {
        exec("UPDATE outbox SET published = FALSE");
    }

    public long count(String table) {
        try (Connection c = db.getConnection(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void outbox(Connection c, String eventId, String type, String orderId, String customer, long total, String state)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO outbox (event_id, type, order_id, customer, total_paise, state) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, eventId);
            ps.setString(2, type);
            ps.setString(3, orderId);
            ps.setString(4, customer);
            ps.setLong(5, total);
            ps.setString(6, state);
            ps.executeUpdate();
        }
    }

    private Optional<OrderRow> query(String sql, String param) {
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new OrderRow(rs.getString("id"), rs.getString("customer"),
                        new Money(rs.getLong("total_paise")), OrderState.valueOf(rs.getString("state")),
                        rs.getString("idempotency_key")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private interface TxWork {
        void run(Connection c) throws SQLException;
    }

    private void inTransaction(TxWork work) {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            try {
                work.run(c);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw new IllegalStateException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void exec(String sql) {
        try (Connection c = db.getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
