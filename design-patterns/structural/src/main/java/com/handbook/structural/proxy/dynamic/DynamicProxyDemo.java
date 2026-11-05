package com.handbook.structural.proxy.dynamic;

import com.handbook.structural.proxy.banking.AccountService;
import com.handbook.structural.proxy.banking.CoreAccountService;
import com.handbook.structural.proxy.dynamic.LoggingProxy.Greeter;

import java.util.List;

public class DynamicProxyDemo {

    public static void main(String[] args) {
        AccountService accounts = LoggingProxy.wrap(new CoreAccountService(), AccountService.class,
                line -> System.out.println("[audit] " + line));
        accounts.balance("SB-1001");
        accounts.withdraw("SB-1001", 1_000);

        Greeter greeter = LoggingProxy.wrap(new Greeter() {
            public String greet(String name) { return "Namaste, " + name; }
            public List<String> languages() { return List.of("hi", "en"); }
        }, Greeter.class, line -> System.out.println("[audit] " + line));
        greeter.greet("Asha");
        greeter.languages();
        System.out.println("Proxy class: " + (greeter.getClass().getSimpleName().startsWith("$Proxy") ? "$ProxyN (generated at runtime)" : greeter.getClass()));
    }
}
