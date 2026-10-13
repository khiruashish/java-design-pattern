# OOP & SOLID Refresher

Design patterns are named, proven ways of applying these principles. If you understand *why* a principle exists, most patterns will feel obvious.

## The four pillars of OOP

| Pillar | One-liner | Java tool |
|---|---|---|
| **Encapsulation** | Hide state, expose behavior | `private` fields, methods that protect invariants |
| **Abstraction** | Depend on *what*, not *how* | `interface`, `abstract class` |
| **Inheritance** | Reuse via "is-a" | `extends` (use sparingly) |
| **Polymorphism** | One call, many behaviors | Overriding, interfaces, `sealed` + `switch` |

## SOLID

=== "S: Single Responsibility"

    A class should have **one reason to change**.

    ```java
    // ❌ Mixes pricing, persistence and notification
    class OrderService {
        void placeOrder(Order o) { /* price + save to DB + send SMS */ }
    }

    // ✅ One job each
    class PriceCalculator { Money total(Order o) { /* ... */ return null; } }
    class OrderRepository { void save(Order o) { /* ... */ } }
    class OrderNotifier   { void orderPlaced(Order o) { /* ... */ } }
    ```

=== "O: Open/Closed"

    **Open for extension, closed for modification.** Add new behavior by adding code, not by editing working code.

    ```java
    // ✅ New discount = new class. No if/else edits.
    interface DiscountPolicy { int apply(int amount); }
    record FlatDiscount(int off) implements DiscountPolicy {
        public int apply(int amount) { return Math.max(0, amount - off); }
    }
    record PercentDiscount(int pct) implements DiscountPolicy {
        public int apply(int amount) { return amount * (100 - pct) / 100; }
    }
    ```

    Patterns that deliver OCP: **Strategy, Decorator, Observer, Template Method, Factory**.

=== "L: Liskov Substitution"

    A subtype must be usable **anywhere** its parent is expected without surprising the caller.

    ```java
    // ❌ Classic violation: Square changes Rectangle's contract
    class Rectangle { void setWidth(int w) {} void setHeight(int h) {} }
    class Square extends Rectangle { /* setWidth also changes height! */ }
    ```

    Rule of thumb: if a subclass throws `UnsupportedOperationException` for a parent method, you probably broke LSP.

=== "I: Interface Segregation"

    Many small, focused interfaces beat one big one.

    ```java
    // ❌ A printer that must "fax"
    interface Machine { void print(); void scan(); void fax(); }

    // ✅
    interface Printer { void print(); }
    interface Scanner { void scan(); }
    ```

=== "D: Dependency Inversion"

    High-level modules depend on **abstractions**, not on concrete classes.

    ```java
    // ✅ CheckoutService doesn't know (or care) which gateway is used
    interface PaymentGateway { boolean charge(String orderId, int amountInr); }

    class CheckoutService {
        private final PaymentGateway gateway;
        CheckoutService(PaymentGateway gateway) { this.gateway = gateway; } // injected
    }
    ```

    This is what Spring's dependency injection gives you for free.

## Other principles you'll be asked about

| Principle | Meaning | Interview sound bite |
|---|---|---|
| **DRY** | Don't Repeat Yourself | "Every piece of knowledge has one authoritative place." |
| **KISS** | Keep It Simple | "The simplest design that meets today's requirements." |
| **YAGNI** | You Aren't Gonna Need It | "I don't add extension points until a second use case appears." |
| **Composition over inheritance** | Prefer "has-a" to "is-a" | "Inheritance is the strongest coupling in Java; composition lets me swap behavior at runtime." |
| **Law of Demeter** | Talk only to your immediate friends | "Avoid `a.getB().getC().doX()` chains." |

!!! question "Interview favourite"
    **"Which SOLID principle does the Strategy pattern support?"**
    Mainly **Open/Closed** (new strategies without editing the context) and **Dependency Inversion** (the context depends on the strategy interface). It also helps **Single Responsibility**, because each algorithm lives in its own class.
