package com.handbook.microservices;

import com.handbook.microservices.saga.core.SagaOrchestrator;
import com.handbook.microservices.saga.core.SagaOrchestrator.Outcome;
import com.handbook.microservices.saga.core.SagaOrchestrator.Step;
import com.handbook.microservices.saga.grocery.GroceryServices.DeliveryService;
import com.handbook.microservices.saga.grocery.GroceryServices.InventoryService;
import com.handbook.microservices.saga.grocery.GroceryServices.PaymentService;
import com.handbook.microservices.saga.grocery.PlaceOrderSaga;
import com.handbook.microservices.saga.grocery.PlaceOrderSaga.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SagaTest {

    @Test
    void failedStepCompensatesCompletedStepsInReverseOrder() {
        List<String> calls = new ArrayList<>();
        Outcome outcome = new SagaOrchestrator(1).run("S", List.of(
                Step.compensatable("a", () -> calls.add("a"), () -> calls.add("undo a")),
                Step.compensatable("b", () -> calls.add("b"), () -> calls.add("undo b")),
                Step.compensatable("c", () -> {
                    throw new IllegalStateException("x");
                }, () -> calls.add("undo c"))));
        assertThat(outcome).isEqualTo(Outcome.COMPENSATED);
        assertThat(calls).containsExactly("a", "b", "undo b", "undo a");
    }

    @Test
    void orderSagaLeavesNoChargeOrReservationWhenDeliveryFails() {
        var inventory = new InventoryService();
        var payments = new PaymentService();
        new PlaceOrderSaga(inventory, payments, new DeliveryService()).place(new Order("O", "MANGO-1KG", 4, 1, "000000"));
        assertThat(inventory.available("MANGO-1KG")).isEqualTo(10);
        assertThat(payments.isCharged("O")).isFalse();
    }
}
