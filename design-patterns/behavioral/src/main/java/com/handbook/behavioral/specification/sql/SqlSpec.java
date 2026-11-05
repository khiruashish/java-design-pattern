package com.handbook.behavioral.specification.sql;

import java.util.ArrayList;
import java.util.List;

/** Challenge solution: specifications that translate themselves into a parameterized SQL WHERE clause. */
public record SqlSpec(String where, List<Object> params) {

    public static SqlSpec eq(String column, Object value) {
        return new SqlSpec(column + " = ?", List.of(value));
    }

    public static SqlSpec lte(String column, Object value) {
        return new SqlSpec(column + " <= ?", List.of(value));
    }

    public static SqlSpec gte(String column, Object value) {
        return new SqlSpec(column + " >= ?", List.of(value));
    }

    public SqlSpec and(SqlSpec other) {
        return combine("AND", other);
    }

    public SqlSpec or(SqlSpec other) {
        return combine("OR", other);
    }

    public SqlSpec not() {
        return new SqlSpec("NOT (" + where + ")", params);
    }

    private SqlSpec combine(String op, SqlSpec other) {
        List<Object> all = new ArrayList<>(params);
        all.addAll(other.params);
        return new SqlSpec("(" + where + " " + op + " " + other.where + ")", List.copyOf(all));
    }
}
