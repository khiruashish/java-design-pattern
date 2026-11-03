package com.handbook.enterprise.di;

import com.handbook.enterprise.di.banking.FixedDeposit.Deposit;
import com.handbook.enterprise.di.banking.FixedDeposit.InterestCalculator;
import com.handbook.enterprise.di.container.Clinic.AppointmentService;
import com.handbook.enterprise.di.container.Clinic.BillingService;
import com.handbook.enterprise.di.container.Clinic.EmrDirectory;
import com.handbook.enterprise.di.container.Clinic.PatientDirectory;
import com.handbook.enterprise.di.container.Clinic.ReminderChannel;
import com.handbook.enterprise.di.container.MiniContainer;
import com.handbook.enterprise.di.grocery.Checkout.CheckoutService;
import com.handbook.enterprise.di.grocery.Checkout.DarkStoreSlots;
import com.handbook.enterprise.di.grocery.Checkout.FakeGateway;
import com.handbook.enterprise.di.grocery.Checkout.RecordingNotifier;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DependencyInjectionTest {

    @Test
    void checkoutCanBeTestedWithInjectedFakes() {
        RecordingNotifier notifier = new RecordingNotifier();
        CheckoutService service = new CheckoutService(new FakeGateway(), new DarkStoreSlots(), notifier);
        assertThat(service.placeOrder("O1", "cust", "560001", 100)).contains("fake-ok");
        assertThat(notifier.sent).singleElement().asString().contains("Today");
    }

    @Test
    void injectedClockMakesInterestDeterministic() {
        Deposit fd = new Deposit("FD", 3_650_000, 10_000, LocalDate.of(2025, 1, 1)); // 100% p.a. for easy maths
        Clock tenDaysLater = Clock.fixed(Instant.parse("2025-01-11T00:00:00Z"), ZoneOffset.UTC);
        assertThat(new InterestCalculator(tenDaysLater).accruedPaise(fd)).isEqualTo(100_000);
    }

    @Test
    void containerAutowiresAndCachesSingletons() {
        MiniContainer c = new MiniContainer()
                .bind(PatientDirectory.class, EmrDirectory.class)
                .bind(ReminderChannel.class, com.handbook.enterprise.di.container.Clinic.WhatsAppChannel.class);
        AppointmentService a = c.get(AppointmentService.class);
        assertThat(a).isSameAs(c.get(AppointmentService.class));
        assertThat(a.sendReminder("P-9", "now")).contains("Asha Rao");
    }

    @Test
    void containerReportsCyclesWithThePath() {
        assertThatThrownBy(() -> new MiniContainer().get(BillingService.class))
                .hasMessage("Circular dependency: BillingService -> InsuranceService -> BillingService");
    }
}
