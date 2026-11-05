package com.handbook.enterprise.unitofwork.grocery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/** Use case: create an order, its lines and the stock decrements, all-or-nothing. */
public final class OrderPlacement {

    public record Order(String id, String customer) { }

    public record Line(String orderId, String sku, int qty) { }

    public record Stock(String sku, int onHand) { }

    static final UnitOfWork.Mapper<Order> ORDERS = new UnitOfWork.Mapper<>() {
        public void insert(Connection c, Order o) throws SQLException {
            exec(c, "INSERT INTO orders (id, customer) VALUES (?, ?)", o.id(), o.customer());
        }
        public void update(Connection c, Order o) { throw new UnsupportedOperationException(); }
    };

    static final UnitOfWork.Mapper<Line> LINES = new UnitOfWork.Mapper<>() {
        public void insert(Connection c, Line l) throws SQLException {
            exec(c, "INSERT INTO order_line (order_id, sku, qty) VALUES (?, ?, ?)", l.orderId(), l.sku(), l.qty());
        }
        public void update(Connection c, Line l) { throw new UnsupportedOperationException(); }
    };

    static final UnitOfWork.Mapper<Stock> STOCK = new UnitOfWork.Mapper<>() {
        public void insert(Connection c, Stock s) { throw new UnsupportedOperationException(); }
        public void update(Connection c, Stock s) throws SQLException {
            exec(c, "UPDATE stock SET on_hand = ? WHERE sku = ?", s.onHand(), s.sku());
        }
    };

    private final UnitOfWork uow;

    public OrderPlacement(UnitOfWork uow) {
        this.uow = uow;
    }

    /** Business logic only registers changes; nothing touches the database until commit. */
    public int place(Order order, List<Line> lines, List<Stock> currentStock) {
        uow.registerNew(order, ORDERS);
        for (Line line : lines) {
            uow.registerNew(line, LINES);
            Stock s = currentStock.stream().filter(x -> x.sku().equals(line.sku())).findFirst().orElseThrow();
            uow.registerDirty(new Stock(s.sku(), s.onHand() - line.qty()), STOCK);
        }
        return uow.commit();
    }

    private static void exec(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.executeUpdate();
        }
    }
}
