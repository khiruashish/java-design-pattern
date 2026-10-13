---
tags:
  - Interview
---

# Top 100 Design Pattern, Architecture & Microservices Questions

The questions asked most often in Java interviews, from campus drives to senior design rounds, with short model answers. Each links to the page with full explanations, three worked examples and code. Levels: 🟢 beginner, 🟡 mid-level, 🔴 senior.

!!! tip "How to practise"
    Cover the answer, say yours out loud in under 60 seconds, then compare. If you can't, read the linked page's **Quick Revision Card**.

## OOP & SOLID (1–8)

??? question "1. 🟢 What are the four pillars of OOP?"
    Encapsulation, abstraction, inheritance, polymorphism. See [OOP & SOLID](../getting-started/oop-solid.md).

??? question "2. 🟢 What does SOLID stand for?"
    Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion.

??? question "3. 🟢 Composition vs. inheritance: which and why?"
    Prefer composition: it changes behavior at runtime and avoids fragile base classes and subclass explosions. Inherit only for true is-a relationships.

??? question "4. 🟡 Give an example of violating Liskov substitution."
    `Square extends Rectangle` where `setWidth` also changes height; code expecting a rectangle breaks. Model them separately or make them immutable.

??? question "5. 🟡 How does dependency inversion relate to dependency injection?"
    DIP: high-level code depends on abstractions it owns. DI is a technique to supply implementations (constructor injection, Spring).

??? question "6. 🟢 What is a design pattern? Name the GoF categories."
    A reusable solution to a recurring design problem. Creational, structural, behavioral.

??? question "7. 🟡 When should you NOT use a design pattern?"
    When the problem it solves isn't present yet (YAGNI); patterns add indirection. See [Anti-Patterns](../anti-patterns.md).

??? question "8. 🔴 How do you decide which pattern fits a problem?"
    Identify what varies and what must stay stable, then pick the pattern that isolates that variation. See the [Pattern Selection Guide](../pattern-selection-guide.md).

## Creational (9–20)

??? question "9. 🟢 How do you write a thread-safe Singleton?"
    Enum, holder idiom, or double-checked locking with `volatile`. See [Singleton](../creational/singleton.md).

??? question "10. 🟡 Why does double-checked locking need `volatile`?"
    Without it, another thread can see a non-null reference to a partially constructed object due to reordering. See [Double-Checked Locking](../concurrency/double-checked-locking.md).

??? question "11. 🟡 How can a Singleton be broken?"
    Reflection, serialization, cloning, multiple class loaders. Enums resist the first three.

??? question "12. 🔴 Is Singleton an anti-pattern?"
    Global mutable state and hidden dependencies make it one when misused; a single instance managed by DI is fine.

??? question "13. 🟢 Factory Method vs. Abstract Factory?"
    Factory Method creates one product; Abstract Factory creates families of related products. See [Factory Method](../creational/factory-method.md), [Abstract Factory](../creational/abstract-factory.md).

??? question "14. 🟢 Static factory method vs. constructor?"
    Static factories have names, can cache or return subtypes (`List.of`, `Optional.of`); constructors always create a new exact type.

??? question "15. 🟢 Why use the Builder pattern?"
    Many optional parameters, immutability, readable construction and validation in `build()`. See [Builder](../creational/builder.md).

??? question "16. 🟡 Builder vs. Java records?"
    Records are perfect for small immutable data; builders still help with many optional fields or step-wise validation.

??? question "17. 🟡 What is the Prototype pattern? Shallow vs. deep copy?"
    Create objects by copying a template. Shallow copies share mutable fields; deep copies duplicate them. Avoid `clone()`; prefer copy constructors. See [Prototype](../creational/prototype.md).

??? question "18. 🟡 When is an Object Pool useful?"
    For expensive, limited resources (DB connections, threads); not for cheap objects. See [Object Pool](../creational/object-pool.md).

??? question "19. 🟡 Which creational patterns does Spring use?"
    Singleton scope, `BeanFactory`/`FactoryBean`, prototype scope, builders (`RestClient.builder()`).

??? question "20. 🔴 How would you design a plugin system that creates implementations at runtime?"
    A factory registry (map of type → supplier) populated via `ServiceLoader` or Spring beans; no switch statements.

## Structural (21–34)

??? question "21. 🟢 What is the Adapter pattern?"
    Converts one interface into another clients expect, e.g. `InputStreamReader`. See [Adapter](../structural/adapter.md).

??? question "22. 🟢 What is the Decorator pattern?"
    Wraps an object with the same interface to add behavior; stackable. `BufferedInputStream`. See [Decorator](../structural/decorator.md).

??? question "23. 🟡 Decorator vs. Proxy?"
    Decorator adds behavior; Proxy controls access (lazy, remote, security, caching). See [Comparisons](../comparisons.md).

??? question "24. 🟢 What is the Facade pattern?"
    A simple entry point over a complex subsystem, e.g. `JdbcTemplate`. See [Facade](../structural/facade.md).

??? question "25. 🟡 Adapter vs. Facade?"
    Adapter makes one interface fit another; Facade simplifies many interfaces into one.

??? question "26. 🟡 How do Spring proxies work, and why doesn't `@Transactional` work on self-invocation?"
    Spring wraps beans in JDK or CGLIB proxies; internal `this.method()` calls bypass the proxy. See [Proxy](../structural/proxy.md).

??? question "27. 🟡 What is the Composite pattern?"
    Treat individual objects and groups uniformly in a tree (menus, file systems, org charts). See [Composite](../structural/composite.md).

??? question "28. 🟡 What is the Bridge pattern?"
    Separates abstraction from implementation so both vary independently (JDBC API + drivers). See [Bridge](../structural/bridge.md).

??? question "29. 🟡 What is the Flyweight pattern? JDK example?"
    Share intrinsic state among many objects; `Integer.valueOf` cache, string interning. See [Flyweight](../structural/flyweight.md).

??? question "30. 🟢 Why is `Integer a = 127, b = 127; a == b` true but 128 false?"
    `Integer.valueOf` caches −128..127 (Flyweight); 128 creates new objects. Use `equals`.

??? question "31. 🟡 Which structural patterns are in Java I/O?"
    Decorator (buffered/data streams), Adapter (`InputStreamReader`), Facade-like utilities (`Files`).

??? question "32. 🔴 How would you add caching to a service without changing its code?"
    A caching Proxy/Decorator implementing the same interface, or Spring `@Cacheable` (proxy-based).

??? question "33. 🔴 What's an Anti-Corruption Layer and how does it relate to Adapter/Facade?"
    A system-level translation boundary combining adapters, facades and translators to keep a foreign model out of your domain. See [ACL](../microservices/anti-corruption-layer.md).

??? question "34. 🟡 What is the Service Locator, and why is DI preferred?"
    A registry you ask for dependencies; it hides dependencies and complicates tests. DI makes them explicit.

## Behavioral (35–54)

??? question "35. 🟢 What is the Strategy pattern?"
    Interchangeable algorithms behind an interface, chosen at runtime (payment methods, pricing). See [Strategy](../behavioral/strategy.md).

??? question "36. 🟡 Strategy vs. State?"
    Strategy is chosen by the client and rarely changes itself; State objects change the context's state as part of behavior. See [Comparisons](../comparisons.md).

??? question "37. 🟢 What is the Observer pattern?"
    Subscribers are notified of changes in a subject; Spring events, listeners. See [Observer](../behavioral/observer.md).

??? question "38. 🟡 How do you avoid memory leaks with observers?"
    Unsubscribe (return a handle), weak references, or scoped lifecycles.

??? question "39. 🟢 What is the Template Method pattern?"
    A base class defines the algorithm skeleton; subclasses fill steps. `AbstractList`, `JdbcTemplate` callbacks. See [Template Method](../behavioral/template-method.md).

??? question "40. 🟡 Template Method vs. Strategy?"
    Inheritance with hooks vs. composition with swappable algorithms. Prefer Strategy for flexibility.

??? question "41. 🟢 What is the Command pattern?"
    Encapsulate a request as an object: queue it, log it, undo it. See [Command](../behavioral/command.md).

??? question "42. 🟡 How do you implement undo/redo?"
    Commands with `undo()` on two stacks, or Memento snapshots. See [Memento](../behavioral/memento.md).

??? question "43. 🟡 What is Chain of Responsibility? Where in Java/Spring?"
    Pass a request along handlers until handled; servlet filters, Spring Security filter chain. See [Chain of Responsibility](../behavioral/chain-of-responsibility.md).

??? question "44. 🟡 What is the State pattern? Example?"
    Behavior changes with internal state; order lifecycle, vending machine. See [State](../behavioral/state.md).

??? question "45. 🟡 What is the Iterator pattern?"
    Sequential access without exposing internals; `Iterator`, `Spliterator`. See [Iterator](../behavioral/iterator.md).

??? question "46. 🟡 What is the Mediator pattern?"
    Objects communicate via a mediator instead of directly; chat rooms, air traffic control, UI dialogs. See [Mediator](../behavioral/mediator.md).

??? question "47. 🔴 What is the Visitor pattern and when is it worth it?"
    Add operations to a stable class hierarchy without modifying it (compilers, ASTs); sealed types + pattern matching are a modern alternative. See [Visitor](../behavioral/visitor.md).

??? question "48. 🟡 What is the Memento pattern?"
    Capture and restore an object's state without exposing internals (drafts, undo). See [Memento](../behavioral/memento.md).

??? question "49. 🔴 What is the Interpreter pattern?"
    Represent a grammar as classes and evaluate sentences (rules engines, filters). See [Interpreter](../behavioral/interpreter.md).

??? question "50. 🟡 What is the Null Object pattern?"
    A do-nothing implementation instead of `null` checks. See [Null Object](../behavioral/null-object.md).

??? question "51. 🟡 What is the Specification pattern?"
    Business rules as composable predicates (`and`, `or`, `not`); Spring Data `Specification`. See [Specification](../behavioral/specification.md).

??? question "52. 🟡 Which behavioral patterns does Spring use?"
    Template Method (`JdbcTemplate`), Observer (events), Strategy (`PlatformTransactionManager`), Chain (filters), Command-like (`Runnable` tasks).

??? question "53. 🔴 How do you design a rules engine for discounts?"
    Specification for eligibility, Strategy for calculation, a combination policy (stackable vs. exclusive). See the [Discount Engine LLD](lld-problems.md#11-grocery-discount-engine).

??? question "54. 🔴 Observer vs. publish/subscribe with a broker?"
    Observer is in-process and synchronous; pub/sub is distributed, async and durable. See [Event-Driven](../architecture/event-driven.md).

## Concurrency (55–66)

??? question "55. 🟢 What is the Producer-Consumer pattern?"
    Producers put work in a bounded queue; consumers take it; back-pressure via blocking. See [Producer-Consumer](../concurrency/producer-consumer.md).

??? question "56. 🟡 How do you size a thread pool?"
    CPU-bound: ~number of cores; I/O-bound: cores × (1 + wait/compute), or virtual threads. See [Thread Pool](../concurrency/thread-pool.md).

??? question "57. 🟡 Why is `Executors.newFixedThreadPool` risky in production?"
    Its queue is unbounded; under overload, memory grows until OOM. Use a bounded `ThreadPoolExecutor` with a rejection policy.

??? question "58. 🟡 `Future` vs. `CompletableFuture`?"
    `Future` only blocks for results; `CompletableFuture` composes async steps (`thenApply`, `thenCombine`, `allOf`) and handles errors. See [Future/Promise](../concurrency/future-promise.md).

??? question "59. 🟡 When do you use a read-write lock?"
    Many readers, few writers, and non-trivial read work. See [Read-Write Lock](../concurrency/read-write-lock.md).

??? question "60. 🟢 Why are immutable objects thread-safe?"
    Their state can't change after construction (with final fields and safe publication), so no synchronization is needed. See [Immutable Object](../concurrency/immutable-object.md).

??? question "61. 🟡 What are virtual threads and when should you use them?"
    Lightweight JVM-managed threads (Java 21) for high-concurrency blocking I/O; not for CPU-bound work. See [Virtual Threads](../concurrency/virtual-threads.md).

??? question "62. 🔴 What is pinning with virtual threads?"
    A virtual thread blocked inside `synchronized` (before JDK 24) or native code holds its carrier thread; use `ReentrantLock` for long blocking sections on older JDKs.

??? question "63. 🟡 `synchronized` vs. `ReentrantLock`?"
    `ReentrantLock` adds tryLock, timeouts, fairness and multiple conditions; `synchronized` is simpler and auto-releases.

??? question "64. 🟡 What does `volatile` guarantee?"
    Visibility and ordering (happens-before) for a variable, not atomicity of compound actions.

??? question "65. 🔴 How do you avoid deadlocks?"
    Consistent lock ordering, timeouts (`tryLock`), fewer locks, immutable data, message passing.

??? question "66. 🟡 `AtomicLong` vs. `LongAdder`?"
    `LongAdder` stripes counters to reduce contention under heavy writes; reads sum cells.

## Architecture (67–76)

??? question "67. 🟢 What is layered architecture?"
    Presentation → business → persistence, dependencies pointing down. See [Layered](../architecture/layered.md).

??? question "68. 🟡 What is hexagonal architecture?"
    A core with ports (interfaces it owns) and adapters for technologies on both sides. See [Hexagonal](../architecture/hexagonal.md).

??? question "69. 🟡 What is Clean Architecture's dependency rule?"
    Source dependencies point inward: entities ← use cases ← adapters ← frameworks. See [Clean](../architecture/clean.md).

??? question "70. 🟡 Hexagonal vs. Clean vs. Onion?"
    Same principle (domain at the center, dependencies inward); different vocabulary and emphasis.

??? question "71. 🟡 What is a modular monolith?"
    One deployable with enforced business modules, each with a public API and private internals. See [Modular Monolith](../architecture/modular-monolith.md).

??? question "72. 🔴 Monolith or microservices for a new product?"
    Usually a modular monolith first; extract services when there's a clear reason (scaling, team autonomy, compliance).

??? question "73. 🟡 What is event-driven architecture?"
    Components communicate via events through a broker; producers don't know consumers. See [Event-Driven](../architecture/event-driven.md).

??? question "74. 🔴 How do you enforce architecture rules?"
    ArchUnit tests, Spring Modulith verification, build modules, code review checklists.

??? question "75. 🔴 What is an architecture decision record (ADR)?"
    A short document capturing a decision, its context, options and consequences, kept with the code.

??? question "76. 🟡 What is MVC and how does Spring MVC implement it?"
    `DispatcherServlet` (front controller) → `@Controller` → model + view (or response body for REST).

## Microservices (77–100)

??? question "77. 🟢 What are microservices?"
    Small, independently deployable services around business capabilities, each owning its data. See [Microservices](../microservices/index.md).

??? question "78. 🟡 How do you decide service boundaries?"
    DDD bounded contexts, business capabilities, data ownership, team ownership. See [Decomposition](../microservices/decomposition.md).

??? question "79. 🟢 What does an API gateway do?"
    Single entry point: routing, auth, rate limiting, request IDs, TLS. See [API Gateway](../microservices/api-gateway.md).

??? question "80. 🟡 API gateway vs. BFF?"
    Gateway: generic edge concerns; BFF: backend tailored to one client type. See [BFF](../microservices/backend-for-frontend.md).

??? question "81. 🟡 Client-side vs. server-side discovery?"
    Caller queries the registry and picks an instance vs. a load balancer/DNS picks. See [Service Discovery](../microservices/service-discovery.md).

??? question "82. 🟢 What is a circuit breaker? States?"
    Stops calls to a failing dependency: CLOSED → OPEN → HALF_OPEN. See [Circuit Breaker](../microservices/circuit-breaker.md).

??? question "83. 🟡 How do retries cause outages?"
    They multiply load (retry storms) and duplicate non-idempotent operations. Use backoff, jitter, budgets and idempotency. See [Retry / Timeout / Bulkhead](../microservices/retry-timeout-bulkhead.md).

??? question "84. 🟡 What is a bulkhead?"
    Limit concurrent calls per dependency so one slow dependency can't exhaust shared resources.

??? question "85. 🟡 Token bucket vs. sliding window rate limiting?"
    Token bucket allows bursts with an average rate; sliding window counts precisely over a moving period. See [Rate Limiter](../microservices/rate-limiter.md).

??? question "86. 🟡 How do you handle transactions across services?"
    [Saga](../microservices/saga.md): local transactions + compensations, orchestrated or choreographed.

??? question "87. 🔴 Orchestration vs. choreography?"
    Central coordinator (visible flow, easier error handling) vs. event reactions (looser coupling, harder to follow).

??? question "88. 🟡 What is the dual-write problem and the outbox fix?"
    DB write + broker publish can't be atomic; write the event to an outbox table in the same transaction and relay it. See [Outbox](../microservices/transactional-outbox.md).

??? question "89. 🟡 How do you make a consumer idempotent?"
    Record processed message IDs with a unique constraint in the same transaction as the effect. See [Idempotent Consumer](../microservices/idempotent-consumer.md).

??? question "90. 🟡 What is CQRS?"
    Separate write models from query-shaped read models updated from events. See [CQRS](../microservices/cqrs.md).

??? question "91. 🔴 What is event sourcing? Pros and cons?"
    Store state as events; audit and time travel vs. complexity and schema evolution. See [Event Sourcing](../microservices/event-sourcing.md).

??? question "92. 🟢 Why database per service?"
    Loose coupling and independent evolution; joins become API composition or read models. See [Database per Service](../microservices/database-per-service.md).

??? question "93. 🟡 How do you migrate a monolith to microservices?"
    [Strangler Fig](../microservices/strangler-fig.md): facade, extract capability by capability, gradual rollout, retire legacy code.

??? question "94. 🟡 What is a service mesh?"
    Sidecar proxies + control plane for mTLS, traffic control and telemetry. See [Sidecar / Service Mesh](../microservices/sidecar-service-mesh.md).

??? question "95. 🟡 How does distributed tracing work?"
    Trace and span IDs propagated in headers (W3C `traceparent`), spans collected into a tree. See [Distributed Tracing](../microservices/distributed-tracing.md).

??? question "96. 🟢 How do you manage configuration across environments?"
    Externalized config (config server/ConfigMaps), profiles, secrets in a vault, refresh for flags. See [Config Server](../microservices/config-server.md).

??? question "97. 🟡 What is an anti-corruption layer?"
    A translation layer that keeps legacy or external models out of your domain. See [ACL](../microservices/anti-corruption-layer.md).

??? question "98. 🔴 How do you guarantee exactly-once processing?"
    You don't get exactly-once delivery; you get exactly-once **effect** with an outbox + idempotent consumers + idempotent external calls.

??? question "99. 🔴 How do you test microservices?"
    Unit tests per service, consumer-driven contract tests (Pact), integration tests with Testcontainers, a few end-to-end tests, chaos testing for resilience.

??? question "100. 🔴 Design checkout for a grocery app. Which patterns do you use?"
    Gateway + BFF, saga (reserve → pay → schedule) with outbox and idempotency keys, circuit breakers and timeouts on payment, CQRS for order history, tracing, rate limits for flash sales. See the [Capstone](../projects/grocery-platform.md).

---

[:material-arrow-left: System design patterns](system-design-patterns.md){ .md-button }
[Next: Revision Cheat Sheet :material-arrow-right:](revision-cheatsheet.md){ .md-button .md-button--primary }
