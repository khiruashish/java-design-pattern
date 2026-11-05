package com.handbook.behavioral.mediator.auction;

public class AuctionDemo {

    public static void main(String[] args) {
        AuctionHouse house = new AuctionHouse("Vintage camera", 5_000, 500);
        AuctionHouse.Bidder anil = house.register("Anil");
        AuctionHouse.Bidder bela = house.register("Bela");
        anil.bid(5_000);
        bela.bid(5_200);
        bela.bid(5_500);
        anil.bid(6_500);
        house.close();
        bela.bid(9_000);
    }
}
