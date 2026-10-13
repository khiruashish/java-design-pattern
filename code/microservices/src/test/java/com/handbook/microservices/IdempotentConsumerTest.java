package com.handbook.microservices;

import com.handbook.microservices.idempotency.apikey.IdempotencyKeyStore;
import com.handbook.microservices.idempotency.apikey.IdempotencyKeyStore.StoredResponse;
import com.handbook.microservices.idempotency.wallet.WalletCreditConsumer;
import com.handbook.microservices.support.Db;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotentConsumerTest {

    @Test
    void duplicateMessageHasNoEffect() throws Exception {
        DataSource db = Db.inMemory("t_wallet", WalletCreditConsumer.SCHEMA);
        var consumer = new WalletCreditConsumer(db);
        assertThat(consumer.onRefundIssued("m1", "C", 100)).isTrue();
        assertThat(consumer.onRefundIssued("m1", "C", 100)).isFalse();
        assertThat(Db.count(db, "SELECT balance FROM wallets WHERE customer = 'C'")).isEqualTo(100);
    }

    @Test
    void idempotencyKeyReturnsStoredResponse() {
        var store = new IdempotencyKeyStore();
        int[] runs = {0};
        StoredResponse a = store.execute("k", "body", () -> new StoredResponse("", 201, "run " + ++runs[0]));
        StoredResponse b = store.execute("k", "body", () -> new StoredResponse("", 201, "run " + ++runs[0]));
        assertThat(b.body()).isEqualTo(a.body());
        assertThat(store.execute("k", "other", () -> null).status()).isEqualTo(422);
    }
}
