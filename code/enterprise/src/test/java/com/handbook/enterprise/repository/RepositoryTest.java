package com.handbook.enterprise.repository;

import com.handbook.enterprise.repository.banking.AccountRepository;
import com.handbook.enterprise.repository.banking.AccountRepository.Account;
import com.handbook.enterprise.repository.grocery.InMemoryProductRepository;
import com.handbook.enterprise.repository.grocery.JdbcProductRepository;
import com.handbook.enterprise.repository.grocery.Product;
import com.handbook.enterprise.repository.grocery.ProductRepository;
import com.handbook.enterprise.repository.logistics.Shipments.CachingShipmentRepository;
import com.handbook.enterprise.repository.logistics.Shipments.DbShipmentRepository;
import com.handbook.enterprise.repository.logistics.Shipments.Shipment;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositoryTest {

    static List<ProductRepository> adapters() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        try (Connection c = ds.getConnection()) {
            c.createStatement().execute("CREATE TABLE product (sku VARCHAR PRIMARY KEY, name VARCHAR, category VARCHAR,"
                    + " price_paise BIGINT, stock INT)");
        }
        return List.of(new InMemoryProductRepository(), new JdbcProductRepository(ds));
    }

    /** One contract, every adapter: the in-memory fake can't drift from the real one. */
    @ParameterizedTest
    @MethodSource("adapters")
    void everyAdapterHonoursTheSameContract(ProductRepository repo) {
        repo.save(new Product("A", "a", "x", 100, 1));
        repo.save(new Product("B", "b", "x", 100, 99));
        repo.save(new Product("A", "a", "x", 100, 5)); // upsert
        assertThat(repo.findBySku("A")).get().extracting(Product::stock).isEqualTo(5);
        assertThat(repo.findBySku("missing")).isEmpty();
        assertThat(repo.lowStock(10)).extracting(Product::sku).containsExactly("A");
    }

    @Test
    void staleWritesAreRejected() {
        AccountRepository repo = new AccountRepository();
        repo.save(new Account("X", 100, 0));
        Account a = repo.findById("X").orElseThrow();
        repo.save(a.withdraw(10));
        assertThatThrownBy(() -> repo.save(a.withdraw(20))).isInstanceOf(ConcurrentModificationException.class);
        assertThat(repo.findById("X").orElseThrow().balancePaise()).isEqualTo(90);
    }

    @Test
    void cachingDecoratorHitsTheDatabaseOnce() {
        DbShipmentRepository db = new DbShipmentRepository().add(new Shipment("A", "P", LocalDate.EPOCH, false));
        CachingShipmentRepository repo = new CachingShipmentRepository(db);
        repo.byAwb("A");
        repo.byAwb("A");
        assertThat(db.queries).isEqualTo(1);
    }
}
