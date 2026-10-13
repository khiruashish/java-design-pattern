package com.handbook.architecture;

import com.handbook.architecture.clean.coupon.entities.Coupon;
import com.handbook.architecture.clean.refill.RefillPrescription;
import com.handbook.architecture.clean.refill.RefillPrescription.Prescription;
import com.handbook.architecture.eventdriven.dlq.RetryingConsumer;
import com.handbook.architecture.eventdriven.iot.MeterWindowing;
import com.handbook.architecture.eventdriven.iot.MeterWindowing.Reading;
import com.handbook.architecture.hexagonal.checkout.adapter.out.InMemoryOrderRepository;
import com.handbook.architecture.hexagonal.checkout.domain.CheckoutService;
import com.handbook.architecture.hexagonal.checkout.domain.Order;
import com.handbook.architecture.hexagonal.checkout.port.in.PlaceOrderUseCase.PlaceOrderCommand;
import com.handbook.architecture.layered.rules.LayerRules;
import com.handbook.architecture.layered.rules.LayerRules.Dependency;
import com.handbook.architecture.modular.cycles.ModuleGraph;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureExamplesTest {

    @Test
    void hexagonCoreRunsWithTestAdaptersOnly() {
        List<String> sms = new ArrayList<>();
        var core = new CheckoutService((c, p, r) -> p < 1000, new InMemoryOrderRepository(), (phone, m) -> sms.add(m));
        assertThat(core.placeOrder(new PlaceOrderCommand("C", "P", 500)).status()).isEqualTo(Order.Status.PAID);
        assertThat(core.placeOrder(new PlaceOrderCommand("C", "P", 5000)).status()).isEqualTo(Order.Status.PAYMENT_DECLINED);
        assertThat(sms).hasSize(2);
    }

    @Test
    void couponEntityCapsDiscount() {
        var coupon = new Coupon("X", 50, 0, 10_000, LocalDate.of(2030, 1, 1));
        assertThat(coupon.evaluate(100_000, LocalDate.of(2026, 1, 1))).isEqualTo(new Coupon.Discount(10_000));
    }

    @Test
    void controlledSubstancesAlwaysNeedADoctor() {
        var repo = RefillPrescription.inMemory(Map.of("R", new Prescription("R", "P", "D", 5, LocalDate.of(2030, 1, 1), true)));
        assertThat(RefillPrescription.refill(repo, "R", "P", LocalDate.of(2026, 1, 1)))
                .isInstanceOf(RefillPrescription.NeedsDoctor.class);
    }

    @Test
    void lateEventsLandInTheirEventTimeWindow() {
        var w = new MeterWindowing(300, 10);
        w.accept(new Reading("M", 10, 2));
        w.accept(new Reading("M", 320, 4));
        w.accept(new Reading("M", 250, 4)); // late
        assertThat(w.advanceWatermark(300)).singleElement().extracting(MeterWindowing.WindowResult::avgKw).isEqualTo(3.0);
    }

    @Test
    void poisonMessageGoesToDlqAndOthersContinue() {
        List<String> handled = new ArrayList<>();
        var consumer = new RetryingConsumer<String>(e -> {
            if (e.equals("bad")) {
                throw new IllegalArgumentException("x");
            }
            handled.add(e);
        }, 2);
        consumer.consume(List.of("a", "bad", "b"));
        assertThat(handled).containsExactly("a", "b");
        assertThat(consumer.deadLetters()).hasSize(1);
    }

    @Test
    void upwardDependencyIsAViolation() {
        var rules = new LayerRules().layer("w").layer("s");
        assertThat(rules.violations(List.of(new Dependency("s.A", "w.B")))).hasSize(1);
        assertThat(rules.violations(List.of(new Dependency("w.B", "s.A")))).isEmpty();
    }

    @Test
    void cycleIsDetected() {
        assertThat(new ModuleGraph().dependsOn("a", "b").dependsOn("b", "a").findCycle()).isPresent();
        assertThat(new ModuleGraph().dependsOn("a", "b").buildOrder()).containsExactly("b", "a");
    }
}
