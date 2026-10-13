---
tags:
  - Guide
---

# Glossary

Short, practical definitions of terms used across the handbook, with links to where they matter.

Abstraction (Bridge)
:   The high-level side of a [Bridge](structural/bridge.md): *what* a thing does (a report's contents), delegating *how* to an implementor (CSV, HTML).

Aggregate (DDD)
:   A cluster of domain objects treated as one unit for changes, with one root entity (an `Order` and its lines). Invariants are enforced inside the aggregate.

Anti-Corruption Layer
:   A set of [Adapters](structural/adapter.md) and translators that keeps an external or legacy system's model out of your domain model.

Back-pressure
:   A way for a slow consumer to tell a fast producer to slow down: a full bounded queue, HTTP 429/503, or `request(n)` in reactive streams. See [Producer-Consumer](concurrency/producer-consumer.md).

Bounded Context (DDD)
:   A boundary inside which a model and its terms have one consistent meaning ("customer" in billing vs. in support).

Bulkhead
:   Isolating resources (thread pools, connection pools, semaphores) per tenant or dependency so one can't exhaust everything. See [Thread Pool](concurrency/thread-pool.md).

Carrier Thread
:   A platform thread that runs [virtual threads](concurrency/virtual-threads.md); virtual threads mount on it while running and unmount when they block.

Circuit Breaker
:   A wrapper that stops calling a failing dependency for a while (open), tests it occasionally (half-open), and resumes when healthy (closed).

Cohesion
:   How closely the responsibilities inside a class or module belong together. High cohesion is good.

Composition over Inheritance
:   Prefer building behavior by combining objects (has-a) instead of extending classes (is-a). The basis of Strategy, Decorator, Bridge and more.

Contract Test
:   A test that checks an integration against the agreed request/response format (Pact, WireMock recordings), so vendor or service changes break the build, not production.

Coupling
:   How much one module depends on the internals of another. Low coupling is good.

CQRS
:   Command Query Responsibility Segregation: separate models for writes (commands) and reads (queries).

Double Dispatch
:   Choosing a method based on two runtime types, the mechanism behind [Visitor](behavioral/visitor.md) (`element.accept(v)` → `v.visit(this)`).

Effectively Immutable
:   An object that's technically mutable but never modified after safe publication.

Eventual Consistency
:   Replicas or services may disagree briefly but converge if no new updates happen.

Extrinsic / Intrinsic State
:   In [Flyweight](structural/flyweight.md): intrinsic state is shared and immutable (an icon); extrinsic state is per use and passed in (position).

Fail-Fast Iterator
:   An iterator that throws `ConcurrentModificationException` when the collection is modified during iteration. See [Iterator](behavioral/iterator.md).

Happens-Before
:   The Java Memory Model rule that guarantees one action's effects are visible to another (unlock → lock, volatile write → read, thread start, `join`).

Hook Method
:   An optional step with a default implementation in a [Template Method](behavioral/template-method.md).

Idempotency
:   Doing an operation twice has the same effect as doing it once. Essential for retries, queues and webhooks. See [Command](behavioral/command.md).

Immutability
:   State that can't change after construction. See [Immutable Object](concurrency/immutable-object.md).

Inversion of Control (IoC)
:   A framework calls your code rather than your code calling the framework ("don't call us, we'll call you").

Liskov Substitution Principle (LSP)
:   Subtypes must be usable wherever their base type is expected, without surprising callers.

Little's Law
:   Average items in a system = arrival rate × average time in the system. Used to size pools and queues.

MVCC
:   Multi-Version Concurrency Control: databases keep row versions so readers see snapshots and don't block writers.

Open/Closed Principle (OCP)
:   Software should be open for extension but closed for modification: add new behavior with new code.

Outbox (Transactional)
:   Write a business change and the event describing it in the same database transaction; a relay publishes the event later. Prevents dual-write inconsistencies.

Pinning
:   A virtual thread that can't unmount from its carrier while blocking (inside `synchronized` before JDK 24, or native frames).

Poison Pill
:   A special item placed on a queue to tell a consumer to stop.

Race Condition
:   A bug where the result depends on the timing of threads (check-then-act, read-modify-write without synchronization).

Saga
:   A sequence of local transactions across services with **compensating** actions to undo completed steps when a later step fails.

Sealed Types
:   Java 17+ classes or interfaces that restrict which classes may extend them, enabling exhaustive `switch` with pattern matching.

Single Responsibility Principle (SRP)
:   A class should have one reason to change.

SOLID
:   Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion. See [OOP & SOLID](getting-started/oop-solid.md).

Structured Concurrency
:   Treating a group of related concurrent tasks as one unit with a clear lifetime: fork, join, cancel siblings on failure (`StructuredTaskScope`).

Thread Confinement
:   Keeping data accessible to only one thread, so no synchronization is needed.

Ubiquitous Language (DDD)
:   The shared vocabulary between developers and domain experts, used in code, conversations and documents.

Value Object
:   An immutable object defined by its values, not identity (`Money`, `Email`, `DateRange`).

Virtual Thread
:   A lightweight, JVM-scheduled thread (Java 21). See [Virtual Threads](concurrency/virtual-threads.md).

YAGNI
:   "You Aren't Gonna Need It": don't build flexibility until it's needed.

---

[:material-arrow-left: Anti-Patterns](anti-patterns.md){ .md-button }
[Next: Interview Preparation :material-arrow-right:](interview/index.md){ .md-button .md-button--primary }
