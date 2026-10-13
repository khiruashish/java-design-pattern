---
tags:
  - Guide
---

# Anti-Patterns

An **anti-pattern** is a common response to a recurring problem that looks reasonable but usually makes things worse. Recognizing them is half the value of learning patterns, and senior interviews often ask "what would you flag in this code review?".

Each entry: **symptoms → why it hurts → what to do instead**.

---

## 1. Design and code anti-patterns

### God Object / God Class

- **Symptoms:** `OrderManager` with 4,000 lines and 120 methods; every feature touches it; merge conflicts daily.
- **Why it hurts:** violates Single Responsibility; impossible to test in isolation; one change breaks unrelated features.
- **Instead:** split by responsibility and bounded context; extract [Strategies](behavioral/strategy.md), [States](behavioral/state.md), a [Facade](structural/facade.md) per use case; keep domain logic in domain objects.

### Golden Hammer (pattern overuse)

- **Symptoms:** a Factory for a class with one implementation; an Abstract Factory with one family; Visitor for two types that never change.
- **Why it hurts:** indirection without benefit; newcomers can't find the code that runs.
- **Instead:** apply YAGNI. Introduce a pattern when the **second** variation arrives. Patterns are responses to forces, not goals.

### Copy-Paste Programming

- **Symptoms:** the same `if` ladder for payment methods in checkout, refunds and reconciliation; a bug fixed in one copy but not the others.
- **Why it hurts:** drift and inconsistent behavior (the classic "fixed it in 51 of 54 places").
- **Instead:** one definition: [Factory Method](creational/factory-method.md), [Template Method](behavioral/template-method.md), [Specification](behavioral/specification.md).

### Primitive Obsession

- **Symptoms:** `double amount`, `String currency`, `String email`, `int status` passed everywhere; validation repeated (or forgotten).
- **Why it hurts:** floating-point money errors, invalid values deep in the system, unclear APIs (`transfer(String, String, double)`).
- **Instead:** small **immutable value objects** (`Money`, `Email`, `OrderId`) with validation in the constructor. See [Immutable Object](concurrency/immutable-object.md).

### Boolean Flag Explosion

- **Symptoms:** `isPaid`, `isShipped`, `isCancelled`, `isRefunded` on one entity; impossible combinations ("cancelled and delivered").
- **Why it hurts:** 2ⁿ states, most invalid; rules scattered.
- **Instead:** a single status with an explicit [State](behavioral/state.md) machine.

### Service Locator and Global State

- **Symptoms:** `AppContext.getInstance().getOrderService()` inside business methods; singletons holding mutable maps.
- **Why it hurts:** hidden dependencies, untestable code, order-dependent tests, concurrency bugs.
- **Instead:** **constructor injection**; DI-managed singletons; immutable shared state. See [Singleton](creational/singleton.md).

### Anemic Domain Model (debated)

- **Symptoms:** entities with only getters and setters; all rules in `*Service` classes that manipulate them.
- **Why it hurts:** invariants can be broken from anywhere (`order.setStatus(DELIVERED)` on a cancelled order).
- **Instead:** put behavior and invariants on the domain objects (`order.cancel()` enforces its rules). Keep services for orchestration.

### Magic Numbers and Strings

- **Symptoms:** `if (code == 51)`, `"356"`, `0.18` scattered across code.
- **Why it hurts:** meaning is lost; values change in one place and not another.
- **Instead:** named constants, enums, configuration, and [Adapters](structural/adapter.md) that translate vendor codes once.

### Swallowed Exceptions / Exceptions as Control Flow

- **Symptoms:** `catch (Exception e) {}`; using exceptions to exit loops; returning `null` on errors.
- **Why it hurts:** silent failures; impossible debugging; NPEs far from the cause.
- **Instead:** let exceptions propagate or translate them into domain errors; return result types (`Optional`, sealed results); use [Null Object](behavioral/null-object.md) only where "nothing" is genuinely correct.

### Lava Flow

- **Symptoms:** dead code and abandoned abstractions nobody dares to delete ("might still be used").
- **Why it hurts:** cognitive load; false leads during incidents.
- **Instead:** delete with tests and usage metrics as a safety net; feature flags with expiry dates.

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
