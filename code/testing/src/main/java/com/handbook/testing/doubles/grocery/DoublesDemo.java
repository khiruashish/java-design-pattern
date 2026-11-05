package com.handbook.testing.doubles.grocery;

import com.handbook.testing.doubles.grocery.SlotReminderService.Customer;
import com.handbook.testing.doubles.grocery.SlotReminderService.FakeCustomerRepository;
import com.handbook.testing.doubles.grocery.SlotReminderService.SpySms;

public class DoublesDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Testing Slot Reminders with a Stub, a Spy and a Fake ===");
        FakeCustomerRepository customers = new FakeCustomerRepository();
        customers.save(new Customer("C1", "+919845000001", "560034", true));
        customers.save(new Customer("C2", "+919845000002", "560034", false));

        SpySms sms = new SpySms();
        SlotReminderService withSlot = new SlotReminderService(SlotReminderService.stubSlots("Today 19:00-20:00"), sms, customers);
        System.out.println("C1 with a free slot : " + withSlot.remind("C1"));
        System.out.println("C2 opted out        : " + withSlot.remind("C2"));

        SlotReminderService noSlot = new SlotReminderService(SlotReminderService.stubSlots(null), sms, customers);
        System.out.println("C1 with no slot     : " + noSlot.remind("C1"));
        System.out.println("Spy recorded        : " + sms.sent);
        System.out.println("No network, no database, no SMS bill: the test needs nothing but the JVM.");
    }
}
