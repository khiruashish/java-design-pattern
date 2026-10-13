package com.handbook.behavioral.mediator.form;

public class FormDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Checkout Address Form ===");
        CheckoutForm form = new CheckoutForm();
        System.out.println("start          : " + form.render());
        form.country.set("India");
        System.out.println("pick India     : " + form.render());
        form.state.set("Karnataka");
        form.postcode.set("560034");
        System.out.println("state + PIN    : " + form.render());
        form.country.set("Germany");
        System.out.println("switch country : " + form.render());
        form.state.set("Berlin");
        form.postcode.set("10115");
        System.out.println("state + PLZ    : " + form.render());
    }
}
