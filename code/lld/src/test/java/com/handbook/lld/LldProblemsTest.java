package com.handbook.lld;

import com.handbook.lld.discount.DiscountEngine;
import com.handbook.lld.discount.DiscountEngine.Cart;
import com.handbook.lld.discount.DiscountEngine.Item;
import com.handbook.lld.discount.DiscountEngine.Rule;
import com.handbook.lld.discount.DiscountEngine.Spec;
import com.handbook.lld.lrucache.LruCache;
import com.handbook.lld.moviebooking.ShowBooking;
import com.handbook.lld.parkinglot.ParkingLot;
import com.handbook.lld.parkinglot.ParkingLot.Vehicle;
import com.handbook.lld.parkinglot.ParkingLot.VehicleType;
import com.handbook.lld.splitwise.ExpenseSharing;
import com.handbook.lld.systemdesign.ConsistentHashRing;
import com.handbook.lld.tictactoe.TicTacToe;
import com.handbook.lld.vending.VendingMachine;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LldProblemsTest {

    private static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void parkingLotNeverPutsACarInASmallSpot() {
        var lot = new ParkingLot(ParkingLot.nearestSmallestFit(), (t, d) -> 0).addFloor(0, 2, 0, 0);
        assertThat(lot.park(new Vehicle("C", VehicleType.CAR), T0)).isEmpty();
        assertThat(lot.park(new Vehicle("B", VehicleType.BIKE), T0)).isPresent();
    }

    @Test
    void vendingMachineGivesCorrectChange() {
        var vm = new VendingMachine().load("A", new VendingMachine.Product("x", 13), 1);
        vm.insert(10);
        vm.insert(10);
        assertThat(vm.select("A")).endsWith("change [5, 2]");
    }

    @Test
    void lruEvictsLeastRecentlyUsed() {
        var c = new LruCache<String, Integer>(2);
        c.put("a", 1);
        c.put("b", 2);
        c.get("a");
        c.put("c", 3);
        assertThat(c.get("b")).isNull();
        assertThat(c.get("a")).isEqualTo(1);
    }

    @Test
    void expenseBalancesAlwaysSumToZero() {
        var s = new ExpenseSharing();
        s.addExpense("a", 1_001, List.of("a", "b", "c"), ExpenseSharing.equal());
        assertThat(s.balances().values().stream().mapToLong(Long::longValue).sum()).isZero();
        assertThat(s.settleUp()).hasSize(2);
    }

    @Test
    void expiredHoldCanBeTakenByAnotherUser() {
        var show = new ShowBooking(List.of("A1"), Duration.ofMinutes(5));
        assertThat(show.hold("u1", List.of("A1"), T0)).isTrue();
        assertThat(show.hold("u2", List.of("A1"), T0.plusSeconds(60))).isFalse();
        assertThat(show.hold("u2", List.of("A1"), T0.plusSeconds(301))).isTrue();
        assertThat(show.confirm("u1", List.of("A1"), T0.plusSeconds(302))).isFalse();
    }

    @Test
    void onlyBestExclusiveDiscountApplies() {
        var engine = new DiscountEngine()
                .add(new Rule("a", Spec.subtotalAtLeast(0), DiscountEngine.flat(100), true))
                .add(new Rule("b", Spec.subtotalAtLeast(0), DiscountEngine.flat(300), true));
        var applied = engine.apply(new Cart("R", List.of(new Item("x", "c", 1, 1_000))));
        assertThat(applied).extracting(DiscountEngine.Applied::rule).containsExactly("b");
    }

    @Test
    void ticTacToeDetectsColumnWin() {
        var g = new TicTacToe(3);
        g.move(0, 0);
        g.move(0, 1);
        g.move(1, 0);
        g.move(1, 1);
        assertThat(g.move(2, 0)).isEqualTo(TicTacToe.Result.X_WINS);
        assertThatThrownBy(() -> g.move(2, 2)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void consistentHashingIsStable() {
        var ring = new ConsistentHashRing(50);
        ring.addNode("n1");
        ring.addNode("n2");
        String before = ring.nodeFor("k");
        assertThat(ring.nodeFor("k")).isEqualTo(before);
        assertThat(Map.of("n1", 1, "n2", 1)).containsKey(before);
    }
}
