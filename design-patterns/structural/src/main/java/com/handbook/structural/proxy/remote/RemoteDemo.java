package com.handbook.structural.proxy.remote;

import com.handbook.structural.proxy.remote.InventoryClientProxy.InventoryUnavailableException;

import java.io.IOException;

public class RemoteDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: Order Service Calling the Inventory Service ===");
        InventoryServer server = new InventoryServer();
        Transport network = request -> {
            System.out.println("  [wire] -> " + request);
            String response = server.handle(request);
            System.out.println("  [wire] <- " + response);
            return response;
        };
        InventoryApi inventory = new InventoryClientProxy(network); // the order service only sees InventoryApi

        System.out.println("Rice in BLR-1: " + inventory.availableUnits("BLR-1", "RICE-5KG"));
        System.out.println("Oil in BLR-1 : " + inventory.availableUnits("BLR-1", "OIL-1L"));

        InventoryApi duringOutage = new InventoryClientProxy(request -> {
            throw new IOException("connect timed out after 2000 ms");
        });
        try {
            duringOutage.availableUnits("BLR-1", "RICE-5KG");
        } catch (InventoryUnavailableException e) {
            System.out.println("During outage: " + e.getMessage());
        }
        System.out.println("Server handled " + server.calls() + " calls");
    }
}
