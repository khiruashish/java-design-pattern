import java.io.*;
import java.lang.reflect.Constructor;
import java.util.concurrent.*;

/** Problem: write a lazy, thread-safe Singleton, then break it (reflection, serialization) and defend it. */
public class SingletonDefended {

    /** Lazy + thread-safe without locks: the JVM initialises Holder only on first access (class-init is synchronized). */
    static final class Config implements Serializable {
        private static boolean created;

        private Config() {
            synchronized (Config.class) {
                if (created) throw new IllegalStateException("use Config.getInstance()");   // defend against reflection
                created = true;
            }
        }

        private static final class Holder { static final Config INSTANCE = new Config(); }

        static Config getInstance() { return Holder.INSTANCE; }

        @Serial private Object readResolve() { return getInstance(); }       // defend against deserialization
    }

    /** Double-checked locking: the classic version interviewers ask you to write. volatile is mandatory. */
    static final class IdGenerator {
        private static volatile IdGenerator instance;
        private long next;

        private IdGenerator() { }

        static IdGenerator getInstance() {
            IdGenerator local = instance;                 // one volatile read on the fast path
            if (local == null) {
                synchronized (IdGenerator.class) {
                    local = instance;
                    if (local == null) instance = local = new IdGenerator();
                }
            }
            return local;
        }

        synchronized long nextId() { return ++next; }
    }

    /** The simplest bullet-proof form: enum. Reflection and serialization are handled by the JVM. */
    enum Registry { INSTANCE; int size() { return 0; } }

    public static void main(String[] args) throws Exception {
        // 1. Many threads racing for the instance all get the same object
        try (ExecutorService pool = Executors.newFixedThreadPool(16)) {
            var futures = new java.util.ArrayList<Future<IdGenerator>>();
            for (int i = 0; i < 100; i++) futures.add(pool.submit(IdGenerator::getInstance));
            IdGenerator first = futures.get(0).get();
            boolean same = true;
            for (var f : futures) same &= f.get() == first;
            System.out.println("100 concurrent getInstance() calls -> same object: " + same);
            check(same, "DCL singleton");
        }

        // 2. Reflection attack
        Config c = Config.getInstance();
        Constructor<Config> ctor = Config.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        try {
            ctor.newInstance();
            check(false, "reflection should be blocked");
        } catch (java.lang.reflect.InvocationTargetException e) {
            System.out.println("reflection attack blocked: " + e.getCause().getMessage());
        }

        // 3. Serialization attack
        var bytes = new ByteArrayOutputStream();
        try (var out = new ObjectOutputStream(bytes)) { out.writeObject(c); }
        Object copy;
        try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { copy = in.readObject(); }
        System.out.println("deserialized copy is the same instance: " + (copy == c));
        check(copy == c, "readResolve");

        System.out.println("enum singleton: " + Registry.INSTANCE + " (reflection on enums throws by design)");
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
