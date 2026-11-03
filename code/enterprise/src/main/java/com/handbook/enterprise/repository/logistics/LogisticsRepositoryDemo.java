package com.handbook.enterprise.repository.logistics;

import com.handbook.enterprise.repository.logistics.Shipments.CachingShipmentRepository;
import com.handbook.enterprise.repository.logistics.Shipments.DbShipmentRepository;
import com.handbook.enterprise.repository.logistics.Shipments.Shipment;
import com.handbook.enterprise.repository.logistics.Shipments.ShipmentRepository;

import java.time.LocalDate;

public class LogisticsRepositoryDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: Domain Finders and a Caching Repository ===");
        DbShipmentRepository db = new DbShipmentRepository()
                .add(new Shipment("AWB-1", "Pune", LocalDate.of(2026, 3, 1), true))
                .add(new Shipment("AWB-2", "Delhi", LocalDate.of(2026, 3, 2), false))
                .add(new Shipment("AWB-3", "Chennai", LocalDate.of(2026, 3, 9), false));
        ShipmentRepository repo = new CachingShipmentRepository(db);

        for (int i = 0; i < 1_000; i++) {
            repo.byAwb("AWB-2"); // customers refreshing the tracking page
        }
        System.out.println("1,000 tracking lookups -> DB queries: " + db.queries);
        System.out.println("Overdue as of 2026-03-05: " + repo.overdue(LocalDate.of(2026, 3, 5)).stream()
                .map(s -> s.awb() + " (" + s.city() + ")").toList());
        System.out.println("DB queries after report: " + db.queries);
    }
}
