package com.handbook.microservices.acl.logistics;

import com.handbook.microservices.acl.logistics.CarrierStatusTranslator.CarrierEvent;

import java.util.List;

public class CarrierAclDemo {

    public static void main(String[] args) {
        var acl = new CarrierStatusTranslator();
        System.out.println("=== Logistics: two carriers, one status model ===");
        for (CarrierEvent e : List.of(
                new CarrierEvent("QuickDrop", "771020", "OFD"),
                new CarrierEvent("BlueKite", "BK-55", "3"),
                new CarrierEvent("QuickDrop", "771020", "dl "),
                new CarrierEvent("BlueKite", "BK-56", "9"),
                new CarrierEvent("BlueKite", "BK-57", "7"))) {
            System.out.printf("%-9s %-7s %-4s -> %s%n", e.carrier(), e.awb(), "'" + e.rawStatus() + "'", acl.translate(e));
        }
    }
}
