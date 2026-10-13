package com.handbook.microservices.dbperservice.replication;

import com.handbook.microservices.dbperservice.replication.ShippingService.CustomerAddressChanged;

public class ReplicationDemo {

    public static void main(String[] args) {
        var shipping = new ShippingService();

        System.out.println("=== Food delivery: shipping keeps its own copy of addresses ===");
        shipping.on(new CustomerAddressChanged("CUST-42", "12 MG Road, Bengaluru", 1));
        System.out.println(shipping.createLabel("ORD-1", "CUST-42"));

        shipping.on(new CustomerAddressChanged("CUST-42", "7 Lake View, Bengaluru", 3));
        shipping.on(new CustomerAddressChanged("CUST-42", "Old flat (v2 arrived late)", 2));
        System.out.println("v3 then a late v2 event arrive -> " + shipping.createLabel("ORD-2", "CUST-42"));

        System.out.println("customer-service is down for maintenance...");
        System.out.println(shipping.createLabel("ORD-3", "CUST-42") + "   (no remote call needed)");
    }
}
