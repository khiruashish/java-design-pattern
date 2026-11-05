package com.handbook.structural.facade;

import com.handbook.structural.facade.banking.AccountOpeningFacade;
import com.handbook.structural.facade.checkout.CheckoutFacade;
import com.handbook.structural.facade.checkout.Subsystems.*;
import com.handbook.structural.facade.ride.RideBookingFacade;
import com.handbook.structural.facade.streaming.PlaybackFacade;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FacadeTest {

    @Test
    void failedPaymentReleasesReservedStock() {
        InventoryService inventory = new InventoryService();
        CheckoutFacade checkout = new CheckoutFacade(inventory, new PaymentService(),
                new DeliveryService(), new NotificationService());
        assertThat(checkout.placeOrder("C", "addr", "BREAD-WW", 5, 9_999).success()).isFalse();
        assertThat(inventory.reserve("BREAD-WW", 5)).isTrue(); // stock came back
    }

    @Test
    void playbackPicksRenditionFromBandwidth() {
        var facade = new PlaybackFacade();
        var slow = new PlaybackFacade.Client("u", "browser", "Chennai", 1_000, false, "en-IN");
        assertThat(facade.start("MOVIE-1", slow).rendition()).isEqualTo("480p");
        assertThat(facade.start("MOVIE-1", slow).cdnEdge()).isEqualTo("edge-maa-1");
        assertThatThrownBy(() -> facade.start("PREMIUM-X", slow)).isInstanceOf(SecurityException.class);
    }

    @Test
    void goodCreditGetsPremiumCard() {
        var facade = new AccountOpeningFacade();
        assertThat(facade.open(new AccountOpeningFacade.Applicant("A", "ABCPK1234Q", "1111"))).contains("Platinum");
        assertThat(facade.open(new AccountOpeningFacade.Applicant("B", "QQQPK1234Q", "1111"))).contains("Classic");
    }

    @Test
    void rideWithoutDriversIsNotCharged() {
        assertThat(new RideBookingFacade().book("R", "Airport T2", 10, false)).contains("not charged");
    }
}
