package com.handbook.springref.outbox;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * A polling outbox relay with JdbcTemplate and KafkaTemplate (requires @EnableScheduling).
 * FOR UPDATE SKIP LOCKED (PostgreSQL, MySQL 8) lets several relay instances run without
 * publishing the same row; .get() waits for the broker's ack before the row is marked.
 */
@Component
public class OutboxRelayJob {

    private final JdbcTemplate jdbc;
    private final KafkaTemplate<String, String> kafka;

    public OutboxRelayJob(JdbcTemplate jdbc, KafkaTemplate<String, String> kafka) {
        this.jdbc = jdbc;
        this.kafka = kafka;
    }

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void relay() throws Exception {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, topic, aggregate_id, payload FROM outbox "
                        + "WHERE published_at IS NULL ORDER BY id LIMIT 100 FOR UPDATE SKIP LOCKED");
        for (Map<String, Object> row : rows) {
            kafka.send((String) row.get("topic"), (String) row.get("aggregate_id"), (String) row.get("payload"))
                    .get(); // at-least-once: if we crash after this, the row is published again
            jdbc.update("UPDATE outbox SET published_at = now() WHERE id = ?", row.get("id"));
        }
    }
}
