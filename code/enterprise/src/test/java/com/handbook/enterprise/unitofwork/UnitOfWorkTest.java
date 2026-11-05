package com.handbook.enterprise.unitofwork;

import com.handbook.enterprise.unitofwork.erp.DirtyCheckingSession;
import com.handbook.enterprise.unitofwork.erp.DirtyCheckingSession.Employee;
import com.handbook.enterprise.unitofwork.grocery.OrderPlacement;
import com.handbook.enterprise.unitofwork.grocery.OrderPlacement.Line;
import com.handbook.enterprise.unitofwork.grocery.OrderPlacement.Order;
import com.handbook.enterprise.unitofwork.grocery.OrderPlacement.Stock;
import com.handbook.enterprise.unitofwork.grocery.UnitOfWork;
import com.handbook.enterprise.unitofwork.travel.IdentityMap;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnitOfWorkTest {

    @Test
    void failedCommitLeavesNothingBehind() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        try (Connection c = ds.getConnection()) {
            c.createStatement().execute("CREATE TABLE orders (id VARCHAR PRIMARY KEY, customer VARCHAR)");
            c.createStatement().execute("CREATE TABLE order_line (order_id VARCHAR, sku VARCHAR, qty INT)");
            c.createStatement().execute("CREATE TABLE stock (sku VARCHAR PRIMARY KEY, on_hand INT CHECK (on_hand >= 0))");
            c.createStatement().execute("INSERT INTO stock VALUES ('A', 1)");
        }
        assertThatThrownBy(() -> new OrderPlacement(new UnitOfWork(ds))
                .place(new Order("O", "c"), List.of(new Line("O", "A", 2)), List.of(new Stock("A", 1))))
                .hasMessageContaining("Rolled back 3 changes");
        try (Connection c = ds.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM orders");
            rs.next();
            assertThat(rs.getInt(1)).isZero();
        }
    }

    @Test
    void identityMapLoadsOncePerId() {
        AtomicInteger loads = new AtomicInteger();
        IdentityMap<String, Object> map = new IdentityMap<>(id -> {
            loads.incrementAndGet();
            return new Object();
        });
        assertThat(map.get("x")).isSameAs(map.get("x"));
        assertThat(loads).hasValue(1);
    }

    @Test
    void dirtyCheckingUpdatesOnlyChangedColumns() {
        Map<String, Employee> table = new HashMap<>(Map.of("E1", new Employee("E1", "HR", 100), "E2", new Employee("E2", "HR", 100)));
        DirtyCheckingSession s = new DirtyCheckingSession(table);
        s.find("E1").salaryPaise = 200;
        s.find("E2");
        assertThat(s.flush()).containsExactly("UPDATE employee SET salary_paise=200 WHERE id='E1'");
        assertThat(s.flush()).isEmpty();
        assertThat(table.get("E1").salaryPaise).isEqualTo(200);
    }
}
