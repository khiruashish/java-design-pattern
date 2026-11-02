import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/** Problem: add caching and timing to ANY interface without writing a class per interface (JDK dynamic proxy). */
public class DynamicProxy {

    interface ProductCatalog {
        String describe(String sku);
        int stock(String sku);
    }

    static final class SlowCatalog implements ProductCatalog {
        int calls;
        public String describe(String sku) { calls++; return "Product " + sku; }
        public int stock(String sku) { calls++; return sku.length(); }
    }

    /** One handler works for every interface: caches results by (method, args). */
    static final class CachingHandler implements InvocationHandler {
        private final Object target;
        private final Map<List<Object>, Object> cache = new HashMap<>();
        final List<String> log = new ArrayList<>();

        CachingHandler(Object target) { this.target = target; }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) return method.invoke(target, args);   // equals/hashCode/toString
            List<Object> key = new ArrayList<>();
            key.add(method.getName());
            if (args != null) key.addAll(Arrays.asList(args));
            if (cache.containsKey(key)) { log.add("HIT  " + key); return cache.get(key); }
            Object result = method.invoke(target, args);
            cache.put(key, result);
            log.add("MISS " + key);
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T caching(Class<T> type, T target, CachingHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    /** Lazy (virtual) proxy: the expensive object is created on first use only. */
    static final class LazyCatalog implements ProductCatalog {
        private final java.util.function.Supplier<ProductCatalog> factory;
        private ProductCatalog real;
        LazyCatalog(java.util.function.Supplier<ProductCatalog> factory) { this.factory = factory; }
        private ProductCatalog real() { if (real == null) real = factory.get(); return real; }
        public String describe(String sku) { return real().describe(sku); }
        public int stock(String sku) { return real().stock(sku); }
        boolean initialised() { return real != null; }
    }

    public static void main(String[] args) {
        SlowCatalog slow = new SlowCatalog();
        CachingHandler handler = new CachingHandler(slow);
        ProductCatalog catalog = caching(ProductCatalog.class, slow, handler);

        catalog.describe("A1"); catalog.describe("A1"); catalog.stock("A1"); catalog.stock("A1"); catalog.describe("B2");
        handler.log.forEach(System.out::println);
        System.out.println("real calls = " + slow.calls + " (5 requests)");
        System.out.println("proxy class = " + (Proxy.isProxyClass(catalog.getClass()) ? "generated $Proxy" : "?"));
        check(slow.calls == 3, "two hits served from cache");

        LazyCatalog lazy = new LazyCatalog(SlowCatalog::new);
        System.out.println("lazy created? " + lazy.initialised());
        lazy.stock("X");
        System.out.println("after first call, created? " + lazy.initialised());
        check(lazy.initialised(), "lazy init");
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
