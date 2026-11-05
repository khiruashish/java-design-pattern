package com.handbook.enterprise.repository.logistics;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Intention-revealing finders plus a caching decorator over the repository port. */
public final class Shipments {

    public record Shipment(String awb, String city, LocalDate promised, boolean delivered) {
    }

    public interface ShipmentRepository {
        Optional<Shipment> byAwb(String awb);
        List<Shipment> overdue(LocalDate asOf); // (1)!
    }

    /** Pretend database that counts round trips. */
    public static final class DbShipmentRepository implements ShipmentRepository {
        private final Map<String, Shipment> rows = new HashMap<>();
        public int queries;

        public DbShipmentRepository add(Shipment s) {
            rows.put(s.awb(), s);
            return this;
        }

        public Optional<Shipment> byAwb(String awb) {
            queries++;
            return Optional.ofNullable(rows.get(awb));
        }

        public List<Shipment> overdue(LocalDate asOf) {
            queries++;
            return rows.values().stream().filter(s -> !s.delivered() && s.promised().isBefore(asOf))
                    .sorted(java.util.Comparator.comparing(Shipment::awb)).toList();
        }
    }

    /** Decorator: same port, adds a read-through cache for hot tracking lookups. */
    public static final class CachingShipmentRepository implements ShipmentRepository { // (2)!
        private final ShipmentRepository inner;
        private final Map<String, Optional<Shipment>> cache = new HashMap<>();

        public CachingShipmentRepository(ShipmentRepository inner) {
            this.inner = inner;
        }

        public Optional<Shipment> byAwb(String awb) { return cache.computeIfAbsent(awb, inner::byAwb); }

        public List<Shipment> overdue(LocalDate asOf) { return inner.overdue(asOf); } // reports always go to the DB
    }

    private Shipments() {
    }
}
