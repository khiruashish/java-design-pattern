package com.handbook.microservices.materializedview.tracking;

import java.util.HashMap;
import java.util.Map;

/** A denormalized view assembled from three services' events; tolerates out-of-order delivery per source. */
public final class TrackingView {

    public sealed interface Event permits Shipped, DriverAssigned, EtaUpdated {
        String awb();
        long version(); // per-source sequence number
    }

    public record Shipped(String awb, String city, long version) implements Event { }

    public record DriverAssigned(String awb, String driver, long version) implements Event { }

    public record EtaUpdated(String awb, String eta, long version) implements Event { }

    public record Page(String awb, String city, String driver, String eta) { }

    private final Map<String, Page> pages = new HashMap<>();
    private final Map<String, Long> lastVersion = new HashMap<>(); // key: awb + source

    public void on(Event e) {
        String source = e.getClass().getSimpleName();
        String key = e.awb() + "/" + source;
        if (e.version() <= lastVersion.getOrDefault(key, 0L)) { // (1)!
            return;
        }
        lastVersion.put(key, e.version());
        Page p = pages.getOrDefault(e.awb(), new Page(e.awb(), "?", "?", "?"));
        pages.put(e.awb(), switch (e) { // (2)!
            case Shipped s -> new Page(p.awb(), s.city(), p.driver(), p.eta());
            case DriverAssigned d -> new Page(p.awb(), p.city(), d.driver(), p.eta());
            case EtaUpdated t -> new Page(p.awb(), p.city(), p.driver(), t.eta());
        });
    }

    public Page page(String awb) {
        return pages.get(awb);
    }
}
