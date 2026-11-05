package com.handbook.structural.proxy.remote;

/** The interface both the client and the server agree on. */
public interface InventoryApi {

    int availableUnits(String warehouse, String sku);
}
