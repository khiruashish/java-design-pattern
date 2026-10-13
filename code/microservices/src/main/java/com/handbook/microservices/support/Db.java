package com.handbook.microservices.support;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

/** Creates in-memory H2 databases so the examples use real JDBC transactions without a server. */
public final class Db {

    private Db() {
    }

    private static final AtomicInteger COUNTER = new AtomicInteger();

    /** A fresh in-memory database (unique per call) that lives until the JVM exits. */
    public static DataSource inMemory(String name, String... ddl) {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + name + "_" + COUNTER.incrementAndGet() + ";DB_CLOSE_DELAY=-1");
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            for (String sql : ddl) {
                s.execute(sql);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return ds;
    }

    public static long count(DataSource ds, String sql) {
        try (Connection c = ds.getConnection(); Statement s = c.createStatement(); var rs = s.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
