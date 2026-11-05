package com.handbook.structural.proxy.dynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/** Challenge solution: one generic proxy that logs every call on ANY interface. */
public final class LoggingProxy {

    private LoggingProxy() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(T target, Class<T> iface, Consumer<String> log) {
        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class<?>[] {iface},
                (proxy, method, args) -> {
                    String call = iface.getSimpleName() + "." + method.getName()
                            + (args == null ? "()" : Arrays.toString(args).replace('[', '(').replace(']', ')'));
                    try {
                        Object result = method.invoke(target, args);
                        log.accept(call + " -> " + result);
                        return result;
                    } catch (InvocationTargetException e) {
                        log.accept(call + " threw " + e.getCause());
                        throw e.getCause(); // (1)!
                    }
                });
    }

    public interface Greeter {
        String greet(String name);

        List<String> languages();
    }
}
