package com.handbook.enterprise.unitofwork.grocery;

import com.handbook.enterprise.unitofwork.grocery.OrderPlacement.Line;
import com.handbook.enterprise.unitofwork.grocery.OrderPlacement.Order;
import com.handbook.enterprise.unitofwork.grocery.OrderPlacement.Stock;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GroceryUowDemo {

    public static void main(String[] args) throws SQLException {
        System.out.println("=== Grocery: Order + Lines + Stock in One Unit of Work ===");
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:uow_grocery;DB_CLOSE_DELAY=-1");
        try (Connection c = ds.getConnection()) {
            c.createStatement().execute("CREATE TABLE orders (id VARCHAR PRIMARY KEY, customer VARCHAR)");
            c.createStatement().execute("CREATE TABLE order_line (order_id VARCHAR, sku VARCHAR, qty INT)");
            c.createStatement().execute("CREATE TABLE stock (sku VARCHAR PRIMARY KEY, on_hand INT CHECK (on_hand >= 0))");
            c.createStatement().execute("INSERT INTO stock VALUES ('MANGO-1KG', 3), ('CURD-400G', 10)");
        }

        int written = new OrderPlacement(new UnitOfWork(ds)).place(new Order("O-1", "riya"),
                List.of(new Line("O-1", "MANGO-1KG", 2), new Line("O-1", "CURD-400G", 1)),
                List.of(new Stock("MANGO-1KG", 3), new Stock("CURD-400G", 10)));
        System.out.println("O-1 committed " + written + " changes -> " + snapshot(ds));

        try {
            new OrderPlacement(new UnitOfWork(ds)).place(new Order("O-2", "arjun"),
                    List.of(new Line("O-2", "CURD-400G", 2), new Line("O-2", "MANGO-1KG", 5)), // only 1 mango left
                    List.of(new Stock("MANGO-1KG", 1), new Stock("CURD-400G", 9)));
        } catch (IllegalStateException e) {
            System.out.println("O-2 failed: " + e.getMessage() + ": stock would go negative");
        }
        System.out.println("After O-2 -> " + snapshot(ds) + " (no orphan order, curd untouched)");
    }

    private static String snapshot(JdbcDataSource ds) throws SQLException {
        try (Connection c = ds.getConnection()) {
            ResultSet o = c.createStatement().executeQuery("SELECT COUNT(*) FROM orders");
            o.next();
            ResultSet l = c.createStatement().executeQuery("SELECT COUNT(*) FROM order_line");
            l.next();
            ResultSet s = c.createStatement().executeQuery("SELECT LISTAGG(sku || '=' || on_hand, ', ') WITHIN GROUP (ORDER BY sku) FROM stock");
            s.next();
            return "orders=" + o.getInt(1) + ", lines=" + l.getInt(1) + ", stock[" + s.getString(1) + "]";
        }
    }
}
