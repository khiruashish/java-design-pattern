package com.handbook.testing.doubles.grocery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Production code under test, plus the three doubles a test needs: a stub, a spy and a fake. */
public final class SlotReminderService {

    public interface SlotApi { Optional<String> nextFreeSlot(String pincode); }

    public interface Sms { void send(String phone, String text); }

    public interface CustomerRepository {
        void save(Customer c);
        Optional<Customer> find(String id);
    }

    public record Customer(String id, String phone, String pincode, boolean optedIn) { }

    private final SlotApi slots;
    private final Sms sms;
    private final CustomerRepository customers;

    public SlotReminderService(SlotApi slots, Sms sms, CustomerRepository customers) {
        this.slots = slots;
        this.sms = sms;
        this.customers = customers;
    }

    public String remind(String customerId) {
        Customer c = customers.find(customerId).orElseThrow();
        if (!c.optedIn()) {
            return "skipped (opted out)";
        }
        return slots.nextFreeSlot(c.pincode()).map(slot -> {
            sms.send(c.phone(), "A delivery slot just opened: " + slot);
            return "reminded for " + slot;
        }).orElse("no slot, no SMS");
    }

    // ---------- Test doubles ----------

    /** STUB: returns canned answers; has no logic and records nothing. */
    public static SlotApi stubSlots(String answer) { // (1)!
        return pincode -> Optional.ofNullable(answer);
    }

    /** SPY: a real-enough implementation that RECORDS how it was used, for assertions afterwards. */
    public static final class SpySms implements Sms { // (2)!
        public final List<String> sent = new ArrayList<>();

        public void send(String phone, String text) {
            sent.add(phone + " <- " + text);
        }
    }

    /** FAKE: a working, simplified implementation (in-memory instead of PostgreSQL). */
    public static final class FakeCustomerRepository implements CustomerRepository { // (3)!
        private final Map<String, Customer> rows = new HashMap<>();

        public void save(Customer c) { rows.put(c.id(), c); }

        public Optional<Customer> find(String id) { return Optional.ofNullable(rows.get(id)); }
    }
}
