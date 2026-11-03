package com.handbook.microservices.materializedview.sales;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** A summary table rebuilt on a schedule: the dashboard reads one row instead of scanning every order line. */
public final class StoreSalesView {

    private final DataSource ds = Db.inMemory("mv_sales",
            "CREATE TABLE order_line (store VARCHAR, sales_day DATE, amount_paise BIGINT)",
            "CREATE TABLE mv_store_daily_sales (store VARCHAR, sales_day DATE, orders BIGINT, revenue_paise BIGINT,"
                    + " PRIMARY KEY (store, sales_day))");

    public void insertLines(String store, String day, int count, long paiseEach) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO order_line VALUES (?, ?, ?)")) {
            for (int i = 0; i < count; i++) {
                ps.setString(1, store);
                ps.setDate(2, Date.valueOf(day));
                ps.setLong(3, paiseEach);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Equivalent of PostgreSQL REFRESH MATERIALIZED VIEW: recompute in one transaction. */
    public void refresh() { // (1)!
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            c.setAutoCommit(false);
            s.execute("DELETE FROM mv_store_daily_sales");
            s.execute("INSERT INTO mv_store_daily_sales SELECT store, sales_day, COUNT(*), SUM(amount_paise)"
                    + " FROM order_line GROUP BY store, sales_day");
            c.commit(); // (2)!
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public String dashboard(String store, String day) {
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT orders, revenue_paise FROM mv_store_daily_sales WHERE store = ? AND sales_day = ?")) {
            ps.setString(1, store);
            ps.setDate(2, Date.valueOf(day));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getLong(1) + " orders, INR " + rs.getLong(2) / 100 : "no data yet";
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public long rawLines() {
        return Db.count(ds, "SELECT COUNT(*) FROM order_line");
    }
}
