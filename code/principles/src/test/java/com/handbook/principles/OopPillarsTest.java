package com.handbook.principles;

import com.handbook.principles.oop.abstraction.CompressingStorage;
import com.handbook.principles.oop.abstraction.InMemoryStorage;
import com.handbook.principles.oop.abstraction.ReportArchiver;
import com.handbook.principles.oop.encapsulation.BankAccount;
import com.handbook.principles.oop.inheritance.Car;
import com.handbook.principles.oop.inheritance.Truck;
import com.handbook.principles.oop.polymorphism.Payment;
import com.handbook.principles.oop.polymorphism.Receipt;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OopPillarsTest {

    @Test
    void encapsulation_protectsInvariant() {
        BankAccount account = new BankAccount("A", 1000);
        account.withdraw(400);
        assertThat(account.balanceCents()).isEqualTo(600);
        assertThatThrownBy(() -> account.withdraw(601)).isInstanceOf(IllegalStateException.class);
        assertThat(account.balanceCents()).isEqualTo(600);
        assertThatThrownBy(() -> account.history().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void abstraction_clientWorksWithAnyImplementation() {
        String body = "hello ".repeat(50);
        assertThat(new ReportArchiver(new InMemoryStorage()).archiveAndReadBack("k", body)).isEqualTo(body);
        assertThat(new ReportArchiver(new CompressingStorage(new InMemoryStorage())).archiveAndReadBack("k", body)).isEqualTo(body);
    }

    @Test
    void inheritance_subclassesSpecialiseTheHook() {
        assertThat(new Car("C1").describe()).isEqualTo("Car C1 (4 wheels) toll=250");
        assertThat(new Truck("T1", 3).describe()).isEqualTo("Truck T1 (8 wheels) toll=1200");
    }

    @Test
    void polymorphism_dispatchOverloadAndSealedSwitch() {
        Payment card = new Payment.Card(10_000, "4242");
        assertThat(card.feeCents()).isEqualTo(200);
        assertThat(Receipt.channel(card)).isEqualTo("card ending 4242");
        assertThat(Receipt.line("fee", 200L)).endsWith("cents");
        assertThat(Receipt.line("rate", 2.0)).endsWith("%");
    }
}
