package com.handbook.microservices;

import com.handbook.microservices.dbperservice.batching.BatchedComposer;
import com.handbook.microservices.dbperservice.grocery.CustomerService;
import com.handbook.microservices.dbperservice.grocery.OrderService;
import com.handbook.microservices.dbperservice.replication.ShippingService;
import com.handbook.microservices.dbperservice.replication.ShippingService.CustomerAddressChanged;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabasePerServiceTest {

    @Test
    void batchedCompositionMakesOneCall() {
        var customers = new CustomerService();
        assertThat(new BatchedComposer(new OrderService(), customers).history()).hasSize(4);
        assertThat(customers.apiCalls()).isEqualTo(1);
    }

    @Test
    void replicaIgnoresOutOfOrderOlderVersions() {
        var shipping = new ShippingService();
        shipping.on(new CustomerAddressChanged("C", "new", 2));
        shipping.on(new CustomerAddressChanged("C", "old", 1));
        assertThat(shipping.createLabel("O", "C")).endsWith("new");
    }
}
