package com.handbook.enterprise.repository.grocery;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Adapter #2: real SQL (H2 here, PostgreSQL in production). All mapping lives here. */
public final class JdbcProductRepository implements ProductRepository {

    private final DataSource ds;

    public JdbcProductRepository(DataSource ds) {
        this.ds = ds;
    }

    public void save(Product p) {
        sql("MERGE INTO product (sku, name, category, price_paise, stock) KEY (sku) VALUES (?, ?, ?, ?, ?)", ps -> { // (1)!
            ps.setString(1, p.sku());
            ps.setString(2, p.name());
            ps.setString(3, p.category());
            ps.setLong(4, p.pricePaise());
            ps.setInt(5, p.stock());
            ps.executeUpdate();
            return null;
        });
    }

    public Optional<Product> findBySku(String sku) {
        return sql("SELECT * FROM product WHERE sku = ?", ps -> {
            ps.setString(1, sku);
            List<Product> found = map(ps.executeQuery());
            return found.stream().findFirst();
        });
    }

    public List<Product> lowStock(int threshold) {
        return sql("SELECT * FROM product WHERE stock < ? ORDER BY sku", ps -> {
            ps.setInt(1, threshold);
            return map(ps.executeQuery());
        });
    }

    private static List<Product> map(ResultSet rs) throws SQLException { // (2)!
        List<Product> out = new ArrayList<>();
        while (rs.next()) {
            out.add(new Product(rs.getString("sku"), rs.getString("name"), rs.getString("category"),
                    rs.getLong("price_paise"), rs.getInt("stock")));
        }
        return out;
    }

    private interface Body<T> { T run(PreparedStatement ps) throws SQLException; }

    private <T> T sql(String sql, Body<T> body) {
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            return body.run(ps);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
