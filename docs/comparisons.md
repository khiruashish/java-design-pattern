---
tags:
  - Guide
  - Interview
---

# Pattern Comparisons

Interviewers love "X vs. Y" questions because they test **understanding**, not memorization. Many patterns have the **same class diagram** and differ only in **intent**. For each pair below: a table, the key difference in one line, and a sentence you can say out loud. Most sections also have a player that runs the patterns side by side, one step at a time, so you can see exactly where they part ways.

---

## Strategy vs. State

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=strategy~state&amp;embed=1" title="Strategy vs. State: animations played side by side" loading="lazy"></iframe>

| | Strategy | State |
|---|---|---|
| Intent | Choose an **algorithm** | Behavior depends on **current state** |
| Who switches | The **client** (or config) | The **state objects themselves**, on events |
| Do variants know each other? | No | Yes: states know their successors |
| Example | Delivery fee: standard / express / member | Order: placed → packed → out for delivery |

> **Say:** "Same structure: a context delegating to an interface. In Strategy the caller picks the behavior; in State the object changes its own behavior as it moves through a lifecycle."

```java
checkout.setFeeStrategy(EXPRESS);   // Strategy: chosen from outside
order.dispatch();                   // State: PACKED decides the next state is OUT_FOR_DELIVERY
```

## Decorator vs. Proxy vs. Adapter vs. Facade

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=decorator~proxy|adapter~facade|decorator~adapter&amp;embed=1" title="Decorator vs. Proxy vs. Adapter vs. Facade: animations played side by side" loading="lazy"></iframe>

| | Decorator | Proxy | Adapter | Facade |
|---|---|---|---|---|
| Interface | **Same** | **Same** | **Different** (converted) | **New, simpler** |
| Wraps | One object | One object | One object | A **subsystem** |
| Purpose | **Add** behavior | **Control access** | **Make it fit** | **Simplify** |
| Stacking | Common (`Fraud(Retry(Logging(x)))`) | Rare | No | No |
| Who creates it | Usually the client | Usually a framework or factory | Integration code | Application code |
| Example | Retry + logging around payments | `@Transactional`, lazy loading | Legacy bank SDK | `CheckoutFacade.placeOrder()` |

> **Say:** "Adapter changes the interface, Decorator keeps it and adds behavior, Proxy keeps it and controls access, Facade gives a new, simpler interface to many classes."

## Factory Method vs. Abstract Factory vs. Builder vs. Prototype

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=factory-method~abstract-factory|builder~prototype&amp;embed=1" title="Factory Method vs. Abstract Factory vs. Builder vs. Prototype: animations played side by side" loading="lazy"></iframe>

| | Factory Method | Abstract Factory | Builder | Prototype |
|---|---|---|---|---|
| Creates | **One** product | A **family** of products | **One complex** product | **Copies** |
| Decides | **Which class** | **Which family** | **How it's assembled** | What to copy |
| Typical API | `create(type)` | `cloud.storage()`, `cloud.queue()` | `Order.builder()...build()` | `template.copy()` |
| Signal | `switch` on type | Products that must match | Many optional parameters | Expensive setup, small changes |

> **Say:** "Factory Method answers *which class*, Abstract Factory *which family*, Builder *how to assemble a complex object step by step*, and Prototype *start from a copy*."

## Template Method vs. Strategy

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=template-method~strategy&amp;embed=1" title="Template Method vs. Strategy: animations played side by side" loading="lazy"></iframe>

| | Template Method | Strategy |
|---|---|---|
| Varies | **Steps** of a fixed algorithm | The **whole** algorithm |
| Mechanism | **Inheritance** (subclass overrides steps) | **Composition** (inject an object or lambda) |
| Bound at | Compile time | Runtime |
| Example | `DataImporter.run()` with `parse()`/`validate()` | `DeliveryFeeStrategy` |

> **Say:** "Template Method fixes the skeleton and lets subclasses fill steps; Strategy swaps the entire algorithm by composition. In modern Java I often get a template's benefits with callbacks, like `JdbcTemplate`."

## Observer vs. Mediator vs. Pub/Sub

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=observer~mediator&amp;embed=1" title="Observer vs. Mediator vs. Pub/Sub: animations played side by side" loading="lazy"></iframe>

| | Observer | Mediator | Pub/Sub (broker) |
|---|---|---|---|
| Shape | Subject → **many** observers | Peers ↔ **one** coordinator | Publishers → topic → subscribers |
| Knowledge | Observers subscribe to the subject | Peers know only the mediator | Both know only the **topic** |
| Logic location | In each observer | **Centralized** in the mediator | In each subscriber |
| Scope | In-process | In-process / orchestrator | Across processes (Kafka, SNS) |

> **Say:** "Observer broadcasts changes; Mediator coordinates peers with central rules; pub/sub is Observer across processes with a broker in between."

## Composite vs. Decorator

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=composite~decorator&amp;embed=1" title="Composite vs. Decorator: animations played side by side" loading="lazy"></iframe>

| | Composite | Decorator |
|---|---|---|
| Children | **Many** | **One** wrapped object |
| Purpose | Part–whole **trees** | Add **behavior** |
| Combines results? | Yes (sum, any, all) | Wraps a single result |
| Example | Bundle of products and bundles | Retry around a payment processor |

> **Say:** "Both use recursive composition. A composite holds many children to represent a whole; a decorator wraps exactly one object to extend it."

## Bridge vs. Adapter vs. Strategy

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=bridge~adapter|bridge~strategy&amp;embed=1" title="Bridge vs. Adapter vs. Strategy: animations played side by side" loading="lazy"></iframe>

| | Bridge | Adapter | Strategy |
|---|---|---|---|
| When designed | **Up front** | **After the fact** | Up front |
| Hierarchies | **Two**, both evolving | One target, one adaptee | One context, many algorithms |
| Purpose | Avoid M × N classes | Make incompatible interfaces work | Swap behavior |
| Example | Reports × renderers (CSV, HTML) | Legacy SDK → `PaymentGateway` | Routing by fee vs. success rate |

> **Say:** "Bridge separates two dimensions that both grow; Adapter retrofits an existing interface; Strategy swaps one algorithm."

## Command vs. Strategy

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=command~strategy&amp;embed=1" title="Command vs. Strategy: animations played side by side" loading="lazy"></iframe>

| | Command | Strategy |
|---|---|---|
| Represents | A **request** (what to do, with its data) | An **algorithm** (how to do it) |
| Lifecycle | Queued, logged, undone, replayed | Called immediately |
| Example | `Transfer(id, from, to, amount)` in a batch queue | `MatchingStrategy.nearest()` |

> **Say:** "A command is a request turned into data so it can be queued, undone or replayed; a strategy is an interchangeable way of computing something."

## Chain of Responsibility vs. Decorator

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=chain-of-responsibility~decorator&amp;embed=1" title="Chain of Responsibility vs. Decorator: animations played side by side" loading="lazy"></iframe>

| | Chain of Responsibility | Decorator |
|---|---|---|
| Forwarding | A handler **may stop** the chain | Usually **always forwards** |
| Purpose | Decide **who handles** | **Add behavior** |
| Example | Loan escalation, validation, servlet filters | Logging and retry layers |

> **Say:** "Structurally similar; the difference is that chain handlers can short-circuit and the goal is finding a handler."

## Singleton vs. Static Utility Class vs. Spring Singleton Bean

| | Singleton (GoF) | Static utility class | Spring singleton bean |
|---|---|---|---|
| Instances | 1 per class loader | 0 | 1 per `ApplicationContext` |
| Implements interfaces / polymorphism | ✅ | ❌ | ✅ |
| Testability (mocking) | Hard | Hard | ✅ Easy |
| State | Allowed | Shouldn't have any | Allowed (thread-safe!) |
| Use for | True per-process invariants | Pure functions (`Math`) | Almost every service |

> **Say:** "In Spring apps I rely on singleton-scoped beans with constructor injection; I reserve the GoF Singleton for rare per-process invariants and use enums or the holder idiom when I do."

## Flyweight vs. Object Pool vs. Singleton

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=flyweight~object-pool|object-pool~singleton&amp;embed=1" title="Flyweight vs. Object Pool vs. Singleton: animations played side by side" loading="lazy"></iframe>

| | Flyweight | Object Pool | Singleton |
|---|---|---|---|
| Instances | Few **shared**, one per distinct state | Several, **borrowed exclusively** | Exactly one |
| Mutable? | **No** | Yes, while borrowed | Maybe |
| Goal | Save **memory** | Reuse **expensive** objects, limit count | One global instance |
| Example | Map icons, `Integer.valueOf` cache | DB connections | Config |

## Interpreter vs. Specification

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=interpreter~specification&amp;embed=1" title="Interpreter vs. Specification: animations played side by side" loading="lazy"></iframe>

| | Interpreter | Specification |
|---|---|---|
| Input | **Text** in a small language (parsed) | Rule **objects** built in code |
| Users | Business users write rules | Developers compose rules |
| Shared structure | AST of AND/OR/comparisons | AND/OR/NOT tree of predicates |

> **Say:** "A specification is like an interpreter without the parser: same rule tree, built in code instead of parsed from text."

## Visitor vs. Sealed Types + Pattern Matching (Java 21)

| | Classic Visitor | Sealed + `switch` |
|---|---|---|
| Boilerplate | `accept()` in every element + visitor interface | None |
| Exhaustiveness | Compiler forces every `visit` overload | Compiler checks the `switch` over sealed types |
| Works across modules / libraries | ✅ | Only if you own the sealed hierarchy |
| Double dispatch over two hierarchies | ✅ | Awkward |

> **Say:** "For closed hierarchies I own, sealed types with pattern matching give me Visitor's safety with far less code; classic visitors remain useful across library boundaries."

## Concurrency comparisons

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=thread-pool~virtual-threads|future-promise~virtual-threads|read-write-lock~immutable-object|double-checked-locking~singleton&amp;embed=1" title="Concurrency comparisons: animations played side by side" loading="lazy"></iframe>

| Question | Short answer |
|---|---|
| **Thread pool vs. virtual threads** | Pools for **CPU-bound** work and hard caps (bulkheads); virtual threads for **I/O-bound** concurrency, with explicit resource limits |
| **`CompletableFuture` vs. virtual threads** | Futures for composition, timeouts and async APIs; virtual threads for simple blocking fan-out with readable code |
| **Read-write lock vs. immutable snapshot** | RW lock when updating in place under a lock; immutable snapshot + `AtomicReference` for wait-free reads of read-mostly data |
| **`synchronized` vs. `ReentrantLock`** | `synchronized` is simpler; `ReentrantLock` adds `tryLock`, timeouts, fairness, multiple conditions, and avoids pinning on JDK 21–23 |
| **DCL vs. holder idiom vs. `computeIfAbsent`** | Instance field: DCL with `volatile`; static: holder; per key: `computeIfAbsent` |
| **`BlockingQueue.put` vs. `offer`** | `put` blocks when full; `offer` returns false (or times out), which is better at system edges |

## Microservices & architecture look-alikes

<iframe class="motion-embed motion-compare" src="../motion/index.html?compare=saga~transactional-outbox|cqrs~materialized-view|event-sourcing~cqrs|api-gateway~backend-for-frontend|circuit-breaker~retry-timeout-bulkhead|layered~hexagonal|hexagonal~clean&amp;embed=1" title="Microservices and architecture look-alikes: animations played side by side" loading="lazy"></iframe>

| Pair | Key difference |
|---|---|
| **Saga vs. Transactional Outbox** | A saga coordinates steps across services and compensates on failure; an outbox makes one service's save-and-publish atomic. Sagas usually use an outbox per step |
| **CQRS vs. Materialized View** | CQRS splits the whole model into write and read sides; a materialized view is one precomputed read table, often the read side of CQRS |
| **Event Sourcing vs. CQRS** | Event sourcing stores changes as events; CQRS separates reads from writes. They pair well but each works alone |
| **API Gateway vs. BFF** | One gateway for every client (routing, auth, limits); a BFF is one backend per client type that shapes the data |
| **Circuit Breaker vs. Retry** | Retry tries again on a brief failure; the breaker stops calling when failures persist. Put retries inside the breaker |
| **Layered vs. Hexagonal** | Layered: business logic depends on the data layer. Hexagonal: the domain owns ports and the database adapter depends on it |
| **Hexagonal vs. Clean** | Same rule (dependencies point at the domain); Clean names four rings, hexagonal just inside and outside |

> **Say:** "A saga is about coordinating many services; an outbox is about not losing one service's event. In practice each saga step publishes through an outbox."

---

[:material-arrow-left: Pattern Selection Guide](pattern-selection-guide.md){ .md-button }
[Next: Anti-Patterns :material-arrow-right:](anti-patterns.md){ .md-button .md-button--primary }
