package com.handbook.behavioral.mediator.form;

import java.util.List;
import java.util.Map;

/**
 * Mediator for a checkout form. Widgets only tell the form "I changed";
 * the form decides what other widgets must do.
 */
public class CheckoutForm {

    private static final Map<String, List<String>> STATES = Map.of(
            "India", List.of("Karnataka", "Maharashtra"), "Germany", List.of("Bavaria", "Berlin"));

    public final Field country = new Field("country", this);
    public final Field state = new Field("state", this);
    public final Field postcode = new Field("postcode", this);
    public final Field payButton = new Field("pay", this);

    public CheckoutForm() {
        state.enabled = false;
        payButton.enabled = false;
    }

    void changed(Field source) { // (1)!
        switch (source.name) {
            case "country" -> {
                state.options = STATES.getOrDefault(country.value, List.of());
                state.value = null;
                state.enabled = !state.options.isEmpty();
                postcode.hint = country.value.equals("India") ? "6-digit PIN" : "5-digit PLZ";
                postcode.value = null;
            }
            case "state", "postcode" -> { /* fall through to validation below */ }
            default -> throw new IllegalArgumentException(source.name);
        }
        payButton.enabled = state.value != null && postcode.value != null
                && postcode.value.length() == (country.value.equals("India") ? 6 : 5);
    }

    public String render() {
        return "country=%s | state=%s %s | postcode=%s (%s) | Pay %s".formatted(
                country.value, state.value, state.enabled ? state.options : "(disabled)",
                postcode.value, postcode.hint, payButton.enabled ? "ENABLED" : "disabled");
    }

    public static class Field {
        final String name;
        final CheckoutForm form;
        String value;
        boolean enabled = true;
        List<String> options = List.of();
        String hint = "";

        Field(String name, CheckoutForm form) {
            this.name = name;
            this.form = form;
        }

        public void set(String newValue) {
            if (!enabled) {
                throw new IllegalStateException(name + " is disabled");
            }
            value = newValue;
            form.changed(this); // the field knows only the mediator
        }
    }
}
