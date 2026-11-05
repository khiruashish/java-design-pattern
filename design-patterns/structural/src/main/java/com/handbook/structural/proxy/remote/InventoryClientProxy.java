package com.handbook.structural.proxy.remote;

import java.io.IOException;

/**
 * Remote proxy (a "client stub"): looks like a local InventoryApi, but marshals each
 * call into a network request and turns network failures into a clear domain error.
 */
public class InventoryClientProxy implements InventoryApi {

    public static class InventoryUnavailableException extends RuntimeException {
        public InventoryUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final Transport transport;

    public InventoryClientProxy(Transport transport) {
        this.transport = transport;
    }

    @Override
    public int availableUnits(String warehouse, String sku) {
        String request = "availableUnits|" + warehouse + "|" + sku; // (1)!
        try {
            String response = transport.send(request);
            String[] parts = response.split("\\|");
            if (!parts[0].equals("OK")) {
                throw new InventoryUnavailableException("Server error: " + response, null);
            }
            return Integer.parseInt(parts[1]); // (2)!
        } catch (IOException e) {
            throw new InventoryUnavailableException("Inventory service unreachable: " + e.getMessage(), e); // (3)!
        }
    }
}
