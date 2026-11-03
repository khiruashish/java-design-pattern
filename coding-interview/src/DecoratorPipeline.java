import java.util.*;

/** Problem: add retries, caching and logging to a price lookup without modifying it or subclassing per combination. */
public class DecoratorPipeline {

    interface PriceService { long priceCents(String sku); }

    /** The real component: flaky on the first call per SKU, to exercise the retry decorator. */
    static final class RemotePriceService implements PriceService {
        final Map<String, Integer> calls = new HashMap<>();
        public long priceCents(String sku) {
            int n = calls.merge(sku, 1, Integer::sum);
            if (n == 1) throw new IllegalStateException("timeout for " + sku);
            return sku.length() * 100L;
        }
    }

    /** Base decorator: holds the wrapped component and forwards to it. */
    abstract static class PriceDecorator implements PriceService {
        protected final PriceService inner;
        PriceDecorator(PriceService inner) { this.inner = inner; }
    }

    static final class Retrying extends PriceDecorator {
        private final int attempts;
        Retrying(PriceService inner, int attempts) { super(inner); this.attempts = attempts; }
        public long priceCents(String sku) {
            RuntimeException last = null;
            for (int i = 1; i <= attempts; i++) {
                try { return inner.priceCents(sku); } catch (RuntimeException e) { last = e; }
            }
            throw last;
        }
    }

    static final class Caching extends PriceDecorator {
        private final Map<String, Long> cache = new HashMap<>();
        Caching(PriceService inner) { super(inner); }
        public long priceCents(String sku) { return cache.computeIfAbsent(sku, inner::priceCents); }
    }

    static final class Logging extends PriceDecorator {
        final List<String> log;
        Logging(PriceService inner, List<String> log) { super(inner); this.log = log; }
        public long priceCents(String sku) {
            long p = inner.priceCents(sku);
            log.add("price(" + sku + ") = " + p);
            return p;
        }
    }

    public static void main(String[] args) {
        RemotePriceService remote = new RemotePriceService();
        List<String> log = new ArrayList<>();
        // Order matters: logging -> caching -> retrying -> remote
        PriceService service = new Logging(new Caching(new Retrying(remote, 3)), log);

        System.out.println("apple  = " + service.priceCents("apple"));
        System.out.println("apple  = " + service.priceCents("apple"));
        System.out.println("banana = " + service.priceCents("banana"));
        System.out.println("remote calls = " + new TreeMap<>(remote.calls));
        log.forEach(l -> System.out.println("log: " + l));

        check(remote.calls.get("apple") == 2, "1 failure + 1 success, then served from cache");
        check(log.size() == 3, "logging is outermost: sees every call");
        try { new Caching(remote).priceCents("kiwi"); check(false, "no retry layer -> failure surfaces"); }
        catch (IllegalStateException e) { System.out.println("without Retrying: " + e.getMessage()); }
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
