package com.handbook.microservices;

import com.handbook.microservices.outbox.grocery.OrderService;
import com.handbook.microservices.outbox.grocery.OutboxRelay;
import com.handbook.microservices.support.Db;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionalOutboxTest {

    @Test
    void orderAndEventCommitOrRollBackTogether() throws Exception {
        DataSource db = Db.inMemory("t_outbox", OrderService.SCHEMA);
        var orders = new OrderService(db);
        orders.placeOrder("O1", "C", 100);
        assertThatThrownBy(() -> orders.placeOrder("O1", "C", 100)).isInstanceOf(java.sql.SQLException.class);
        assertThat(Db.count(db, "SELECT COUNT(*) FROM orders")).isEqualTo(1);
        assertThat(Db.count(db, "SELECT COUNT(*) FROM outbox")).isEqualTo(1);
    }

    @Test
    void relayPublishesInOrderAndMarksRows() throws Exception {
        DataSource db = Db.inMemory("t_relay", OrderService.SCHEMA);
        var orders = new OrderService(db);
        orders.placeOrder("O1", "C", 100);
        orders.placeOrder("O2", "C", 200);
        List<String> published = new ArrayList<>();
        assertThat(new OutboxRelay(db, (k, p) -> published.add(k)).poll(10)).isEqualTo(2);
        assertThat(published).containsExactly("O1", "O2");
        assertThat(new OutboxRelay(db, (k, p) -> published.add(k)).poll(10)).isZero();
    }
}
