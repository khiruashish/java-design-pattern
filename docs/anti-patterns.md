---
tags:
  - Guide
---

# Anti-Patterns

An **anti-pattern** is a common response to a recurring problem that looks reasonable but usually makes things worse. Recognizing them is half the value of learning patterns, and senior interviews often ask "what would you flag in this code review?".

Each entry: **symptoms → why it hurts → what to do instead**. Each design anti-pattern also has a generic **before/after code example** (expand it) and a link to the principle it violates. The principles themselves are covered in [OOP, SOLID & Design Principles](getting-started/oop-solid.md).

---

## 1. Design and code anti-patterns

### God Object / God Class

- **Symptoms:** `OrderManager` with 4,000 lines and 120 methods; every feature touches it; merge conflicts daily.
- **Why it hurts:** violates Single Responsibility; impossible to test in isolation; one change breaks unrelated features.
- **Instead:** split by responsibility and bounded context; extract [Strategies](behavioral/strategy.md), [States](behavioral/state.md), a [Facade](structural/facade.md) per use case; keep domain logic in domain objects.

<iframe class="motion-embed" src="../motion/index.html?p=ap-god-object&amp;embed=1" title="Animated walkthrough of the God Object / God Class anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-god-object){ target=_blank }

??? example "Before / after: God Object"

    === "❌ Before"

        ```java
        class OrderManager {                       // 4,000 lines, 120 methods
            void placeOrder(Order o)  { /* price, tax, stock, payment, email, PDF, audit */ }
            void refund(Order o)      { /* duplicates half of placeOrder */ }
            String renderInvoice(Order o) { /* HTML */ }
            void syncToErp()          { /* nightly batch */ }
        }
        ```

    === "✅ After"

        ```java
        class PlaceOrderUseCase {                   // orchestrates one use case
            PlaceOrderUseCase(Pricing pricing, Inventory inventory, Payments payments, OrderEvents events) { /* ... */ }
            OrderId place(Cart cart) {
                Price price = pricing.price(cart);
                inventory.reserve(cart.items());
                payments.charge(cart.customer(), price);
                return events.placed(cart, price);
            }
        }
        // Pricing, Inventory, Payments, InvoiceRenderer, ErpSync: one reason to change each (SRP)
        ```

    **Principle restored:** [SRP](getting-started/solid/srp.md)

### Golden Hammer (pattern overuse)

- **Symptoms:** a Factory for a class with one implementation; an Abstract Factory with one family; Visitor for two types that never change.
- **Why it hurts:** indirection without benefit; newcomers can't find the code that runs.
- **Instead:** apply YAGNI. Introduce a pattern when the **second** variation arrives. Patterns are responses to forces, not goals.

<iframe class="motion-embed" src="../motion/index.html?p=ap-golden-hammer&amp;embed=1" title="Animated walkthrough of the Golden Hammer (pattern overuse) anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-golden-hammer){ target=_blank }

??? example "Before / after: Golden Hammer"

    === "❌ Before"

        ```java
        interface GreetingFactory { Greeting create(); }          // one implementation, forever
        class DefaultGreetingFactory implements GreetingFactory {
            public Greeting create() { return new Greeting("Hello"); }
        }
        class GreetingFactoryProvider { static GreetingFactory get() { return new DefaultGreetingFactory(); } }

        String msg = GreetingFactoryProvider.get().create().text();
        ```

    === "✅ After"

        ```java
        record Greeting(String text) { }

        String msg = new Greeting("Hello").text();   // add a factory when a 2nd variant really appears
        ```

    **Principle restored:** [YAGNI](getting-started/design-principles.md#3-yagni-you-arent-gonna-need-it), [KISS](getting-started/design-principles.md#2-kiss-keep-it-simple)

### Copy-Paste Programming

- **Symptoms:** the same `if` ladder for payment methods in checkout, refunds and reconciliation; a bug fixed in one copy but not the others.
- **Why it hurts:** drift and inconsistent behavior (the classic "fixed it in 51 of 54 places").
- **Instead:** one definition: [Factory Method](creational/factory-method.md), [Template Method](behavioral/template-method.md), [Specification](behavioral/specification.md).

<iframe class="motion-embed" src="../motion/index.html?p=ap-copy-paste&amp;embed=1" title="Animated walkthrough of the Copy-Paste Programming anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-copy-paste){ target=_blank }

??? example "Before / after: Copy-Paste Programming"

    === "❌ Before"

        ```java
        // checkout
        long fee = method.equals("CARD") ? amount * 2 / 100 : method.equals("UPI") ? 0 : 50;
        // refunds: copied, then "fixed" for UPI only here
        long fee = method.equals("CARD") ? amount * 2 / 100 : method.equals("UPI") ? 5 : 50;
        ```

    === "✅ After"

        ```java
        enum PaymentMethod {
            CARD { long fee(long amount) { return amount * 2 / 100; } },
            UPI  { long fee(long amount) { return 0; } },
            CASH { long fee(long amount) { return 50; } };
            abstract long fee(long amount);
        }
        long fee = method.fee(amount);   // checkout, refunds and reconciliation share ONE rule
        ```

    **Principle restored:** [DRY](getting-started/design-principles.md#1-dry-dont-repeat-yourself)

### Primitive Obsession

- **Symptoms:** `double amount`, `String currency`, `String email`, `int status` passed everywhere; validation repeated (or forgotten).
- **Why it hurts:** floating-point money errors, invalid values deep in the system, unclear APIs (`transfer(String, String, double)`).
- **Instead:** small **immutable value objects** (`Money`, `Email`, `OrderId`) with validation in the constructor. See [Immutable Object](concurrency/immutable-object.md).

<iframe class="motion-embed" src="../motion/index.html?p=ap-primitive-obsession&amp;embed=1" title="Animated walkthrough of the Primitive Obsession anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-primitive-obsession){ target=_blank }

??? example "Before / after: Primitive Obsession"

    === "❌ Before"

        ```java
        void transfer(String from, String to, double amount, String currency) { ... }

        transfer("ACC-2", "ACC-1", 0.1 + 0.2, "usd");   // swapped accounts? 0.30000000000000004? lower-case currency?
        ```

    === "✅ After"

        ```java
        record AccountId(String value) { AccountId { if (!value.matches("ACC-\\d+")) throw new IllegalArgumentException(value); } }
        record Money(long minorUnits, Currency currency) {
            Money { if (minorUnits < 0) throw new IllegalArgumentException("negative"); Objects.requireNonNull(currency); }
        }
        void transfer(AccountId from, AccountId to, Money amount) { ... }   // the compiler catches swapped types
        ```

    **Principle restored:** [Fail Fast](getting-started/design-principles.md#7-fail-fast), [Encapsulation](getting-started/oop-pillars.md#1-encapsulation)

### Boolean Flag Explosion

- **Symptoms:** `isPaid`, `isShipped`, `isCancelled`, `isRefunded` on one entity; impossible combinations ("cancelled and delivered").
- **Why it hurts:** 2ⁿ states, most invalid; rules scattered.
- **Instead:** a single status with an explicit [State](behavioral/state.md) machine.

<iframe class="motion-embed" src="../motion/index.html?p=ap-boolean-flags&amp;embed=1" title="Animated walkthrough of the Boolean Flag Explosion anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-boolean-flags){ target=_blank }

??? example "Before / after: Boolean Flag Explosion"

    === "❌ Before"

        ```java
        class Order {
            boolean paid, shipped, delivered, cancelled, refunded;   // 2^5 = 32 combinations, ~6 valid
        }
        if (order.cancelled && order.delivered) { /* ??? */ }
        ```

    === "✅ After"

        ```java
        enum OrderStatus { CREATED, PAID, SHIPPED, DELIVERED, CANCELLED, REFUNDED }

        class Order {
            private OrderStatus status = OrderStatus.CREATED;
            void cancel() {
                if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED)
                    throw new IllegalStateException("cannot cancel a " + status + " order");
                status = OrderStatus.CANCELLED;
            }
        }
        ```

    **Principle restored:** [Encapsulation](getting-started/oop-pillars.md#1-encapsulation), [State](behavioral/state.md)

### Service Locator and Global State

- **Symptoms:** `AppContext.getInstance().getOrderService()` inside business methods; singletons holding mutable maps.
- **Why it hurts:** hidden dependencies, untestable code, order-dependent tests, concurrency bugs.
- **Instead:** **constructor injection**; DI-managed singletons; immutable shared state. See [Singleton](creational/singleton.md).

<iframe class="motion-embed" src="../motion/index.html?p=ap-service-locator&amp;embed=1" title="Animated walkthrough of the Service Locator and Global State anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-service-locator){ target=_blank }

??? example "Before / after: Service Locator and Global State"

    === "❌ Before"

        ```java
        class InvoiceService {
            long total(Order o) {
                TaxService tax = AppContext.getInstance().get(TaxService.class);   // hidden dependency
                return tax.apply(o.subtotal());
            }
        }
        ```

    === "✅ After"

        ```java
        class InvoiceService {
            private final TaxService tax;
            InvoiceService(TaxService tax) { this.tax = tax; }   // visible, final, replaceable in tests
            long total(Order o) { return tax.apply(o.subtotal()); }
        }
        ```

    **Principle restored:** [DIP](getting-started/solid/dip.md)

### Anemic Domain Model (debated)

- **Symptoms:** entities with only getters and setters; all rules in `*Service` classes that manipulate them.
- **Why it hurts:** invariants can be broken from anywhere (`order.setStatus(DELIVERED)` on a cancelled order).
- **Instead:** put behavior and invariants on the domain objects (`order.cancel()` enforces its rules). Keep services for orchestration.

<iframe class="motion-embed" src="../motion/index.html?p=ap-anemic-domain&amp;embed=1" title="Animated walkthrough of the Anemic Domain Model (debated) anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-anemic-domain){ target=_blank }

??? example "Before / after: Anemic Domain Model"

    === "❌ Before"

        ```java
        class Account { private long balance; public long getBalance(){...} public void setBalance(long b){...} }

        class AccountService {
            void withdraw(Account a, long amount) {
                if (a.getBalance() < amount) throw new IllegalStateException();   // rule lives outside...
                a.setBalance(a.getBalance() - amount);                             // ...and anyone can skip it
            }
        }
        ```

    === "✅ After"

        ```java
        class Account {
            private long balance;
            void withdraw(long amount) {
                if (amount <= 0 || amount > balance) throw new IllegalStateException("invalid withdrawal " + amount);
                balance -= amount;                     // the only way to change the balance
            }
            long balance() { return balance; }
        }
        ```

    **Principle restored:** [Tell, Don't Ask](getting-started/design-principles.md#6-tell-dont-ask)

### Magic Numbers and Strings

- **Symptoms:** `if (code == 51)`, `"356"`, `0.18` scattered across code.
- **Why it hurts:** meaning is lost; values change in one place and not another.
- **Instead:** named constants, enums, configuration, and [Adapters](structural/adapter.md) that translate vendor codes once.

<iframe class="motion-embed" src="../motion/index.html?p=ap-magic-numbers&amp;embed=1" title="Animated walkthrough of the Magic Numbers and Strings anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-magic-numbers){ target=_blank }

??? example "Before / after: Magic Numbers and Strings"

    === "❌ Before"

        ```java
        if (resp.code() == 51) retryLater();
        long tax = amount * 18 / 100;
        if (country.equals("356")) { ... }
        ```

    === "✅ After"

        ```java
        enum GatewayCode { INSUFFICIENT_FUNDS(51), DO_NOT_HONOR(5); final int code; GatewayCode(int c){ code = c; } }
        static final int GST_PERCENT = 18;                  // or injected configuration
        static final String INDIA_ISO_NUMERIC = "356";

        if (resp.code() == GatewayCode.INSUFFICIENT_FUNDS.code) retryLater();
        ```

    **Principle restored:** [DRY](getting-started/design-principles.md#1-dry-dont-repeat-yourself)

### Swallowed Exceptions / Exceptions as Control Flow

- **Symptoms:** `catch (Exception e) {}`; using exceptions to exit loops; returning `null` on errors.
- **Why it hurts:** silent failures; impossible debugging; NPEs far from the cause.
- **Instead:** let exceptions propagate or translate them into domain errors; return result types (`Optional`, sealed results); use [Null Object](behavioral/null-object.md) only where "nothing" is genuinely correct.

<iframe class="motion-embed" src="../motion/index.html?p=ap-swallowed-exceptions&amp;embed=1" title="Animated walkthrough of the Swallowed Exceptions / Exceptions as Control Flow anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-swallowed-exceptions){ target=_blank }

??? example "Before / after: Swallowed Exceptions"

    === "❌ Before"

        ```java
        User find(String id) {
            try {
                return repo.load(id);
            } catch (Exception e) {
                return null;                  // swallowed: NPE three layers up, no log, no cause
            }
        }
        ```

    === "✅ After"

        ```java
        Optional<User> find(String id) {
            return repo.findById(id);         // "not found" is a normal result, not an exception
        }

        User load(String id) {
            try {
                return repo.load(id);
            } catch (SQLException e) {
                throw new UserLookupFailed(id, e);   // translate, keep the cause
            }
        }
        ```

    **Principle restored:** [Fail Fast](getting-started/design-principles.md#7-fail-fast)

### Lava Flow

- **Symptoms:** dead code and abandoned abstractions nobody dares to delete ("might still be used").
- **Why it hurts:** cognitive load; false leads during incidents.
- **Instead:** delete with tests and usage metrics as a safety net; feature flags with expiry dates.

<iframe class="motion-embed" src="../motion/index.html?p=ap-lava-flow&amp;embed=1" title="Animated walkthrough of the Lava Flow anti-pattern" loading="lazy"></iframe>

It opens on **The anti-pattern** causing trouble; switch to **The fix** to see the cure in action. [Open in the explorer](motion/index.html#ap-lava-flow){ target=_blank }

??? example "Before / after: Lava Flow"

    === "❌ Before"

        ```java
        // TODO remove after v2 migration (2019)
        @Deprecated class LegacyPriceEngine { ... }      // still wired, nobody knows if it's called
        if (FeatureFlags.isOn("new-pricing-2019")) newEngine.price(c); else legacy.price(c);
        ```

    === "✅ After"

        ```java
        // 1. Measure: a counter or log on the legacy path shows zero calls for 30 days.
        // 2. Delete the flag, the branch and LegacyPriceEngine in one commit, covered by tests.
        Price price = pricingEngine.price(cart);
        // 3. New flags get an owner and an expiry date in their definition.
        ```

    **Principle restored:** [YAGNI](getting-started/design-principles.md#3-yagni-you-arent-gonna-need-it)

## 2. Concurrency anti-patterns

| Anti-pattern | Symptoms | Instead |
|---|---|---|
| **Unbounded queues** | `new LinkedBlockingQueue<>()`, `Executors.newFixedThreadPool`; OOM under load | Bounded queues with an explicit full policy ([Producer-Consumer](concurrency/producer-consumer.md), [Thread Pool](concurrency/thread-pool.md)) |
| **DCL without `volatile`** | Rare NPEs or half-initialized objects under load | `volatile`, holder idiom, `computeIfAbsent` ([DCL](concurrency/double-checked-locking.md)) |
| **I/O while holding a lock** | Periodic latency spikes; threads waiting on locks | Prepare outside the lock, swap inside ([Read-Write Lock](concurrency/read-write-lock.md)) |
| **Shared mutable state** | Races, "impossible" values | [Immutable objects](concurrency/immutable-object.md), confinement, concurrent collections |
| **Busy waiting** | `while (!ready) {}` burning CPU | `BlockingQueue`, `CountDownLatch`, `CompletableFuture`, `wait/notify` |
| **`if` around `wait()`** | Lost or spurious wakeups, hangs | Always `while (!condition) wait();` |
| **Blocking in the common pool** | Parallel streams and async tasks freeze together | Dedicated executor or virtual threads for I/O ([Future / Promise](concurrency/future-promise.md)) |
| **Removing implicit limits** | Virtual threads overwhelm the database | Explicit semaphores and pools ([Virtual Threads](concurrency/virtual-threads.md)) |
| **ThreadLocal leaks** | User A's tenant ID shows up in user B's request | `try/finally remove()`, `ScopedValue`, context propagation |

## 3. Distributed-system and microservice anti-patterns

| Anti-pattern | Symptoms | Why it hurts | Instead |
|---|---|---|---|
| **Distributed monolith** | Services must deploy together; one change touches 6 repos | All the costs of microservices, none of the benefits | Boundaries by business capability; async events; consumer-driven contracts |
| **Shared database** | Several services read and write the same tables | Hidden coupling; schema changes break other teams | Database per service; integrate through APIs and events |
| **Chatty services** | One page makes 40 synchronous calls | Latency and failure probability multiply | Coarse-grained APIs, BFF/[Facade](structural/facade.md), caching, batching |
| **Synchronous call chains** | A → B → C → D, all blocking | Availability = product of all availabilities; cascading failures | Async messaging, timeouts, circuit breakers, bulkheads |
| **Dual writes** | Save to DB, then publish to Kafka (or call another service) | One succeeds, the other fails: inconsistency | Transactional outbox / CDC |
| **Retry storms** | Retries at every layer, no backoff | A slowdown becomes an outage (5 × 3 × 3 = 45 calls) | One retry layer, exponential backoff with jitter, retry budgets, circuit breakers |
| **Missing idempotency** | Duplicate charges after timeouts | At-least-once delivery is the norm | Idempotency keys, de-duplication, conditional updates |
| **Silent defaults** | Unknown status codes treated as success; `default:` in critical switches | Revenue leaks and data corruption found months later | Fail loudly; exhaustive `switch`; alerts on unknown values |
| **Client-trusted state** | Prices or permissions in cookies or tokens without signatures | Tampering | Server-side state; signed tokens (HMAC); re-validate on the server |

## 4. A code-review checklist

Use this when reviewing pull requests, and in "review this code" interview rounds:

- [ ] Does each class have **one reason to change**?
- [ ] Are there `switch`/`if` chains on **types** or **statuses** repeated in several places?
- [ ] Are money, IDs and emails **value objects**, or raw primitives?
- [ ] Are shared objects **immutable**, or is mutable state shared between threads?
- [ ] Are all queues, pools and caches **bounded**, with a defined overload behavior?
- [ ] Are external calls protected by **timeouts**, and are retries **idempotent**?
- [ ] Do unknown inputs (codes, types, states) **fail loudly**?
- [ ] Is anything written to two systems without an **outbox** or compensation?
- [ ] Is there a pattern here with only **one** implementation and no realistic second (pattern overuse)?

---

[:material-arrow-left: Pattern Comparisons](comparisons.md){ .md-button }
[Next: Glossary :material-arrow-right:](glossary.md){ .md-button .md-button--primary }
