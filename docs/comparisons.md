---
tags:
  - Guide
  - Interview
---

# Pattern Comparisons

Interviewers love "X vs. Y" questions because they test **understanding**, not memorization. Many patterns have the **same class diagram** and differ only in **intent**. For each pair below: a table, the key difference in one line, and a sentence you can say out loud.

---

## Strategy vs. State

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

| | Factory Method | Abstract Factory | Builder | Prototype |
|---|---|---|---|---|
| Creates | **One** product | A **family** of products | **One complex** product | **Copies** |
| Decides | **Which class** | **Which family** | **How it's assembled** | What to copy |
| Typical API | `create(type)` | `cloud.storage()`, `cloud.queue()` | `Order.builder()...build()` | `template.copy()` |
| Signal | `switch` on type | Products that must match | Many optional parameters | Expensive setup, small changes |

> **Say:** "Factory Method answers *which class*, Abstract Factory *which family*, Builder *how to assemble a complex object step by step*, and Prototype *start from a copy*."

## Template Method vs. Strategy

| | Template Method | Strategy |
|---|---|---|
| Varies | **Steps** of a fixed algorithm | The **whole** algorithm |
| Mechanism | **Inheritance** (subclass overrides steps) | **Composition** (inject an object or lambda) |
| Bound at | Compile time | Runtime |
| Example | `DataImporter.run()` with `parse()`/`validate()` | `DeliveryFeeStrategy` |

> **Say:** "Template Method fixes the skeleton and lets subclasses fill steps; Strategy swaps the entire algorithm by composition. In modern Java I often get a template's benefits with callbacks, like `JdbcTemplate`."

## Observer vs. Mediator vs. Pub/Sub

| | Observer | Mediator | Pub/Sub (broker) |
|---|---|---|---|
| Shape | Subject → **many** observers | Peers ↔ **one** coordinator | Publishers → topic → subscribers |
| Knowledge | Observers subscribe to the subject | Peers know only the mediator | Both know only the **topic** |
| Logic location | In each observer | **Centralized** in the mediator | In each subscriber |
| Scope | In-process | In-process / orchestrator | Across processes (Kafka, SNS) |

> **Say:** "Observer broadcasts changes; Mediator coordinates peers with central rules; pub/sub is Observer across processes with a broker in between."

## Composite vs. Decorator

| | Composite | Decorator |
|---|---|---|
| Children | **Many** | **One** wrapped object |
| Purpose | Part–whole **trees** | Add **behavior** |
| Combines results? | Yes (sum, any, all) | Wraps a single result |
| Example | Bundle of products and bundles | Retry around a payment processor |

> **Say:** "Both use recursive composition. A composite holds many children to represent a whole; a decorator wraps exactly one object to extend it."

## Bridge vs. Adapter vs. Strategy

| | Bridge | Adapter | Strategy |
|---|---|---|---|
| When designed | **Up front** | **After the fact** | Up front |
| Hierarchies | **Two**, both evolving | One target, one adaptee | One context, many algorithms |
| Purpose | Avoid M × N classes | Make incompatible interfaces work | Swap behavior |
| Example | Reports × renderers (CSV, HTML) | Legacy SDK → `PaymentGateway` | Routing by fee vs. success rate |

> **Say:** "Bridge separates two dimensions that both grow; Adapter retrofits an existing interface; Strategy swaps one algorithm."

## Command vs. Strategy

| | Command | Strategy |
|---|---|---|
| Represents | A **request** (what to do, with its data) | An **algorithm** (how to do it) |
| Lifecycle | Queued, logged, undone, replayed | Called immediately |
| Example | `Transfer(id, from, to, amount)` in a batch queue | `MatchingStrategy.nearest()` |

> **Say:** "A command is a request turned into data so it can be queued, undone or replayed; a strategy is an interchangeable way of computing something."

## Chain of Responsibility vs. Decorator

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

| | Flyweight | Object Pool | Singleton |
|---|---|---|---|
| Instances | Few **shared**, one per distinct state | Several, **borrowed exclusively** | Exactly one |
| Mutable? | **No** | Yes, while borrowed | Maybe |
| Goal | Save **memory** | Reuse **expensive** objects, limit count | One global instance |
| Example | Map icons, `Integer.valueOf` cache | DB connections | Config |

## Interpreter vs. Specification

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

| Question | Short answer |
|---|---|
| **Thread pool vs. virtual threads** | Pools for **CPU-bound** work and hard caps (bulkheads); virtual threads for **I/O-bound** concurrency, with explicit resource limits |
| **`CompletableFuture` vs. virtual threads** | Futures for composition, timeouts and async APIs; virtual threads for simple blocking fan-out with readable code |
| **Read-write lock vs. immutable snapshot** | RW lock when updating in place under a lock; immutable snapshot + `AtomicReference` for wait-free reads of read-mostly data |
| **`synchronized` vs. `ReentrantLock`** | `synchronized` is simpler; `ReentrantLock` adds `tryLock`, timeouts, fairness, multiple conditions, and avoids pinning on JDK 21–23 |
| **DCL vs. holder idiom vs. `computeIfAbsent`** | Instance field: DCL with `volatile`; static: holder; per key: `computeIfAbsent` |
| **`BlockingQueue.put` vs. `offer`** | `put` blocks when full; `offer` returns false (or times out), which is better at system edges |

---

[:material-arrow-left: Pattern Selection Guide](pattern-selection-guide.md){ .md-button }
[Next: Anti-Patterns :material-arrow-right:](anti-patterns.md){ .md-button .md-button--primary }
