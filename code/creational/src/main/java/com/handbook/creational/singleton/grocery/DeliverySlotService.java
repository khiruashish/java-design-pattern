package com.handbook.creational.singleton.grocery;

public class DeliverySlotService {

    private final StoreConfig config = StoreConfig.INSTANCE;

    public boolean expressAvailable() {
        return config.isEnabled("express.delivery");
    }

    StoreConfig config() {
        return config;
    }
}
