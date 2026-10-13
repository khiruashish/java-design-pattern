package com.handbook.behavioral.memento.booking;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BookingDemo {

    public static void main(String[] args) {
        System.out.println("=== Travel: Resume a Hotel Booking on Another Device ===");
        BookingWizard onPhone = new BookingWizard();
        onPhone.answer("city", "Goa");
        onPhone.answer("dates", "2026-12-27..2026-12-31");
        System.out.println("phone  : " + onPhone);

        String token = onPhone.saveDraft(); // stored against the user's account
        System.out.println("draft token stored (" + token.length() + " chars)");

        BookingWizard onLaptop = BookingWizard.resume(token);
        System.out.println("laptop : " + onLaptop);
        onLaptop.answer("guests", "2 adults");
        System.out.println("laptop : " + onLaptop);

        String oldToken = Base64.getUrlEncoder().encodeToString("v=1;step=2;city=Pune".getBytes(StandardCharsets.UTF_8));
        try {
            BookingWizard.resume(oldToken);
        } catch (IllegalArgumentException e) {
            System.out.println("old draft: " + e.getMessage());
        }
    }
}
