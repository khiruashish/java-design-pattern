package com.handbook.behavioral.mediator;

import com.handbook.behavioral.mediator.form.CheckoutForm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MediatorTest {

    @Test
    void stateIsDisabledUntilCountryChosen() {
        CheckoutForm form = new CheckoutForm();
        assertThatThrownBy(() -> form.state.set("Karnataka")).hasMessageContaining("disabled");
        form.country.set("India");
        form.state.set("Karnataka");
        assertThat(form.render()).contains("state=Karnataka");
    }

    @Test
    void changingCountryResetsDependentFieldsAndPayButton() {
        CheckoutForm form = new CheckoutForm();
        form.country.set("India");
        form.state.set("Karnataka");
        form.postcode.set("560034");
        assertThat(form.render()).endsWith("Pay ENABLED");
        form.country.set("Germany");
        assertThat(form.render()).contains("state=null", "postcode=null").endsWith("Pay disabled");
    }

    @Test
    void wrongPostcodeLengthKeepsPayDisabled() {
        CheckoutForm form = new CheckoutForm();
        form.country.set("Germany");
        form.state.set("Berlin");
        form.postcode.set("560034");
        assertThat(form.render()).endsWith("Pay disabled");
    }
}
