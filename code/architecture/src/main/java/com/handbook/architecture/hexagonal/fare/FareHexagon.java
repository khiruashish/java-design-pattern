package com.handbook.architecture.hexagonal.fare;

import java.util.Map;
import java.util.function.Function;

/** The fare core and its driven port for routing, with three interchangeable adapters. */
public final class FareHexagon {

    private FareHexagon() {
    }

    public record Point(double lat, double lon) {
    }

    public record Route(double km, int minutes, String source) {
    }

    /** Driven port, owned by the core and expressed in its units (km, minutes). */
    public interface RoutePort {
        Route route(Point from, Point to);
    }

    /** The core: fare rules only. */
    public static final class FareCalculator {
        private final RoutePort routes;

        public FareCalculator(RoutePort routes) {
            this.routes = routes;
        }

        public String quote(Point from, Point to) {
            Route r = routes.route(from, to);
            long paise = 5_000 + Math.round(r.km() * 1_200) + r.minutes() * 150L; // base + per km + per minute
            return String.format("₹%d.%02d for %.1f km / %d min (route from %s)",
                    paise / 100, paise % 100, r.km(), r.minutes(), r.source());
        }
    }

    /** Adapter for a (fictional) online maps API returning meters and seconds as JSON-ish maps. */
    public static final class GeoMapsAdapter implements RoutePort {
        private final Function<String, Map<String, Long>> api;

        public GeoMapsAdapter(Function<String, Map<String, Long>> api) {
            this.api = api;
        }

        @Override
        public Route route(Point from, Point to) {
            Map<String, Long> json = api.apply(from.lat() + "," + from.lon() + ";" + to.lat() + "," + to.lon());
            return new Route(json.get("distance_m") / 1000.0, (int) (json.get("duration_s") / 60), "GeoMaps traffic"); // (1)!
        }
    }

    /** Adapter that needs no network: straight-line distance × detour factor, average city speed. */
    public static final class OfflineEstimateAdapter implements RoutePort {
        @Override
        public Route route(Point from, Point to) {
            double dLat = StrictMath.toRadians(to.lat() - from.lat());
            double dLon = StrictMath.toRadians(to.lon() - from.lon());
            double a = StrictMath.pow(StrictMath.sin(dLat / 2), 2) + StrictMath.cos(StrictMath.toRadians(from.lat()))
                    * StrictMath.cos(StrictMath.toRadians(to.lat())) * StrictMath.pow(StrictMath.sin(dLon / 2), 2);
            double km = 6371 * 2 * StrictMath.atan2(StrictMath.sqrt(a), StrictMath.sqrt(1 - a)) * 1.3; // (2)!
            return new Route(km, (int) Math.ceil(km / 22 * 60), "offline estimate");
        }
    }

    /** Adapters compose: try the primary, fall back to the secondary. The core never knows. */
    public static final class FallbackRouteAdapter implements RoutePort {
        private final RoutePort primary;
        private final RoutePort fallback;

        public FallbackRouteAdapter(RoutePort primary, RoutePort fallback) {
            this.primary = primary;
            this.fallback = fallback;
        }

        @Override
        public Route route(Point from, Point to) {
            try {
                return primary.route(from, to);
            } catch (RuntimeException e) {
                return fallback.route(from, to); // (3)!
            }
        }
    }
}
