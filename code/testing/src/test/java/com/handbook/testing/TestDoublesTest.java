package com.handbook.testing;

import com.handbook.testing.doubles.grocery.SlotReminderService;
import com.handbook.testing.doubles.grocery.SlotReminderService.Customer;
import com.handbook.testing.doubles.grocery.SlotReminderService.FakeCustomerRepository;
import com.handbook.testing.doubles.grocery.SlotReminderService.SpySms;
import com.handbook.testing.doubles.payments.MockGateway;
import com.handbook.testing.doubles.payments.RefundService;
import com.handbook.testing.doubles.travel.BookingService;
import com.handbook.testing.doubles.travel.FaultyFakeGateway;
import com.handbook.testing.doubles.travel.FaultyFakeGateway.Fault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestDoublesTest {

    FakeCustomerRepository customers;
    SpySms sms;

    @BeforeEach
    void setUp() {
        customers = new FakeCustomerRepository();
        sms = new SpySms();
    }

    @Test
    void optedInCustomerGetsExactlyOneSms() {
        customers.save(new Customer("C", "+91", "560001", true));
        new SlotReminderService(SlotReminderService.stubSlots("18:00"), sms, customers).remind("C");
        assertThat(sms.sent).containsExactly("+91 <- A delivery slot just opened: 18:00");
    }

    @Test
    void optedOutCustomerGetsNothing() {
        customers.save(new Customer("C", "+91", "560001", false));
        new SlotReminderService(SlotReminderService.stubSlots("18:00"), sms, customers).remind("C");
        assertThat(sms.sent).isEmpty();
    }

    @Test
    void refundIsCappedAtWhatRemains() {
        MockGateway mock = new MockGateway().expect("refund(p, 100)");
        new RefundService(mock).refundMissingItems("p", 1_000, 900, 500);
        assertThat(mock.verify()).startsWith("verified");
    }

    @Test
    void timeoutsAreRetriedButSoldOutIsNot() {
        FaultyFakeGateway api = new FaultyFakeGateway().failNext(Fault.TIMEOUT).failNext(Fault.SOLD_OUT);
        assertThat(new BookingService(api).book("h", "n")).isEqualTo("sold out");
        assertThat(api.calls()).isEqualTo(2);
    }
}
