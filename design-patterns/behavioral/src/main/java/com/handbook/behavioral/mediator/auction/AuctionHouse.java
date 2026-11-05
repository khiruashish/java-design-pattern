package com.handbook.behavioral.mediator.auction;

import java.util.ArrayList;
import java.util.List;

/** Challenge solution: bidders never see each other; the auction house mediates every bid. */
public class AuctionHouse {

    private final String lot;
    private final int minIncrement;
    private final List<Bidder> bidders = new ArrayList<>();
    private int highest;
    private Bidder leader;
    private boolean closed;

    public AuctionHouse(String lot, int reservePrice, int minIncrement) {
        this.lot = lot;
        this.highest = reservePrice;
        this.minIncrement = minIncrement;
    }

    public Bidder register(String name) {
        Bidder b = new Bidder(name, this);
        bidders.add(b);
        return b;
    }

    void bid(Bidder bidder, int amount) {
        if (closed) {
            bidder.tell("auction closed");
        } else if (amount < highest + (leader == null ? 0 : minIncrement)) {
            bidder.tell("bid INR " + amount + " rejected, minimum is INR " + (highest + (leader == null ? 0 : minIncrement)));
        } else {
            Bidder previous = leader;
            highest = amount;
            leader = bidder;
            bidder.tell("you lead at INR " + amount);
            if (previous != null && previous != bidder) {
                previous.tell("outbid, new price INR " + amount);
            }
        }
    }

    public void close() {
        closed = true;
        String result = leader == null ? "unsold" : "sold to " + leader.name + " for INR " + highest;
        bidders.forEach(b -> b.tell(lot + " " + result));
    }

    public static class Bidder {
        private final String name;
        private final AuctionHouse house;

        Bidder(String name, AuctionHouse house) {
            this.name = name;
            this.house = house;
        }

        public void bid(int amount) {
            house.bid(this, amount);
        }

        void tell(String message) {
            System.out.println("  -> " + name + ": " + message);
        }
    }
}
