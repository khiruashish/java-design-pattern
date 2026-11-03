package com.handbook.enterprise.unitofwork.grocery;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Collects new/changed/removed objects during a use case and writes them in ONE transaction. */
public final class UnitOfWork {

    /** How to write one kind of object. The unit of work doesn't know SQL itself. */
    public interface Mapper<T> {
        void insert(Connection c, T obj) throws SQLException;
        void update(Connection c, T obj) throws SQLException;
    }

    private record Pending<T>(String kind, T obj, Mapper<T> mapper) {
        void apply(Connection c) throws SQLException {
            if (kind.equals("INSERT")) {
                mapper.insert(c, obj);
            } else {
                mapper.update(c, obj);
            }
        }
    }

    private final DataSource ds;
    private final List<Pending<?>> pending = new ArrayList<>();

    public UnitOfWork(DataSource ds) {
        this.ds = ds;
    }

    public <T> void registerNew(T obj, Mapper<T> mapper) { pending.add(new Pending<>("INSERT", obj, mapper)); } // (1)!

    public <T> void registerDirty(T obj, Mapper<T> mapper) { pending.add(new Pending<>("UPDATE", obj, mapper)); }

    public int commit() {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false); // (2)!
            try {
                for (Pending<?> p : pending) {
                    p.apply(c);
                }
                c.commit();
                return pending.size();
            } catch (SQLException e) {
                c.rollback(); // (3)!
                throw new IllegalStateException("Rolled back " + pending.size() + " changes (SQLState " + e.getSQLState() + ")", e);
            } finally {
                pending.clear();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
