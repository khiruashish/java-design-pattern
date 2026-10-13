---
tags:
  - Interview
---

# Google: Design Patterns in Interviews

!!! warning "Commonly reported, not official"
    This page summarizes publicly shared candidate experiences and Google's own general hiring guidance. Formats vary by role, level and location and change over time. Confirm the details of your loop with your recruiter.

## What the process commonly looks like

- **Coding interviews** dominate for software engineers: data structures and algorithms, written in a shared doc or editor, with emphasis on correctness, complexity and clear code.
- **System design** is commonly added for experienced (roughly L5+) candidates.
- **Behavioral / "Googleyness & leadership"** conversations explore collaboration, ambiguity and impact.
- Google's careers site describes evaluating general cognitive ability, role-related knowledge, leadership and "Googleyness". Feedback is typically reviewed by a hiring committee rather than decided by one interviewer.

## Where design patterns show up

Google interviews rarely ask "define the Visitor pattern". Patterns show up **implicitly**:

| Situation | Pattern knowledge that helps |
|---|---|
| A coding problem with extensible rules ("now support a new type of discount") | [Strategy](../behavioral/strategy.md), [Factory Method](../creational/factory-method.md), open/closed design |
| Iterating complex structures lazily | [Iterator](../behavioral/iterator.md), generators, [Composite](../structural/composite.md) |
| Designing an API or library | [Builder](../creational/builder.md), [Immutable Object](../concurrency/immutable-object.md), [Facade](../structural/facade.md) |
| Concurrency follow-ups | [Producer-Consumer](../concurrency/producer-consumer.md), [Thread Pool](../concurrency/thread-pool.md), [Read-Write Lock](../concurrency/read-write-lock.md) |
| System design | [Rate Limiter](../microservices/rate-limiter.md), caching, sharding, [CQRS](../microservices/cqrs.md), [Circuit Breaker](../microservices/circuit-breaker.md) |

## What interviewers look for

| Signal | What strong looks like |
|---|---|
| Problem solving | Clarifies, explores examples, chooses a sound approach, states complexity |
| Code quality | Readable names, small functions, correct edge cases, no over-engineering |
| Testing | Walks through examples, finds own bugs, mentions test cases |
| Communication | Thinks aloud, takes hints well, explains trade-offs |
| Design (senior) | Requirements → API → data model → scaling → failure modes, with numbers |

!!! tip "Pattern advice for Google-style coding rounds"
    Use patterns only when they **simplify** the solution. An interface plus two implementations for an extensibility follow-up is great; a factory of builders for a 30-line problem is not. Say why you chose the structure.

## 15 practice questions (with model answers)

??? question "1. Design an iterator that flattens a nested list of integers lazily."
    Keep a stack of iterators; `hasNext()` advances until the top iterator yields an integer, pushing nested lists as you meet them. O(1) amortized per element, O(depth) memory. This is [Iterator](../behavioral/iterator.md) over a [Composite](../structural/composite.md).

??? question "2. Implement an LRU cache with O(1) get and put. How would you make it thread-safe?"
    HashMap + doubly linked list (or `LinkedHashMap` with `accessOrder=true` and `removeEldestEntry`). Thread safety: a single lock is simplest; for high concurrency, shard into N segments by key hash. See the [LRU Cache LLD](lld-problems.md#4-lru-cache).

??? question "3. A pricing function grows a new `if` every sprint. Refactor it."
    Extract each rule into a `PricingRule` [Strategy](../behavioral/strategy.md) (or [Specification](../behavioral/specification.md) + action), register rules in a list or map, and test each independently. New rules don't touch existing code.

??? question "4. Design a rate limiter for an API used by millions of clients."
    Token bucket per key, Redis with atomic Lua scripts for global limits, local buckets for hot paths, `429` with `Retry-After`. See [Rate Limiter](../microservices/rate-limiter.md).

??? question "5. Make an expensive, immutable configuration object available everywhere, loaded once."
    Lazy holder idiom or an enum singleton; better, dependency injection with a single instance. Immutable, so no locking after construction. See [Singleton](../creational/singleton.md) and [Immutable Object](../concurrency/immutable-object.md).

??? question "6. Implement an event system where components subscribe to typed events."
    `Map<Class<?>, List<Consumer<?>>>`, type-safe subscribe/publish, copy-on-write lists for concurrent publishing, and unsubscribe handles to avoid leaks. See [Observer](../behavioral/observer.md).

??? question "7. Design a bounded blocking queue without `java.util.concurrent` collections."
    Array + head/tail indices + `ReentrantLock` with two `Condition`s (`notFull`, `notEmpty`); always wait in a `while` loop. See [Producer-Consumer](../concurrency/producer-consumer.md).

??? question "8. Design a text editor's undo/redo."
    [Command](../behavioral/command.md) objects with `execute`/`undo` on two stacks, or [Memento](../behavioral/memento.md) snapshots for complex state; merge consecutive typing commands.

??? question "9. How would you parse and evaluate expressions like `price > 100 AND category = 'fruit'`?"
    Tokenize → recursive-descent parser → AST of composite nodes → evaluate with an [Interpreter](../behavioral/interpreter.md) or [Visitor](../behavioral/visitor.md). Discuss precedence and error messages.

??? question "10. Design a URL shortener. Which parts would you cache and how?"
    ID generation (counter + base62 or random with collision check), key-value store sharded by key, cache-aside for hot links with TTL, 301 vs 302 trade-off, analytics via async events. See [System design patterns](system-design-patterns.md).

??? question "11. A service calls three backends; p99 latency is too high. What do you do?"
    Call in parallel ([Future/Promise](../concurrency/future-promise.md)), add per-call timeouts and hedged requests for idempotent reads, cache, and make optional calls degradable. See [BFF](../microservices/backend-for-frontend.md).

??? question "12. How do you guarantee only one instance runs a scheduled job across a cluster?"
    Leader election (lease in a DB/etcd/ZooKeeper with fencing tokens), or a DB row lock (`SELECT ... FOR UPDATE SKIP LOCKED`), not a JVM singleton.

??? question "13. Builder vs. telescoping constructors vs. static factories: when each?"
    Static factories for simple named creation; builders for many optional parameters and validation across fields; avoid telescoping constructors. See [Builder](../creational/builder.md).

??? question "14. Design a thread-safe counter used by 64 threads at very high rate."
    `LongAdder` (striped cells) beats `AtomicLong` under contention; reads sum the cells. Explain the trade-off (reads are not atomic snapshots).

??? question "15. Tell me about a time you simplified an over-engineered design."
    STAR: what the abstraction cost (onboarding, bugs), the evidence you gathered, how you removed it safely (tests first), the measurable result. See the anti-patterns page: [Anti-Patterns](../anti-patterns.md).

## 4-week plan

| Week | Do |
|---|---|
| 1 | 30 algorithm problems (arrays, hashing, two pointers, trees, graphs); code cleanly with small helper classes |
| 2 | 20 more problems + 4 design-flavoured coding problems (iterator, LRU, rate limiter, event bus) from this handbook |
| 3 | Concurrency patterns; 2 system design mocks (URL shortener, news feed) using [System design patterns](system-design-patterns.md) |
| 4 | Mock interviews with a timer; 5 STAR stories; revise the [cheat sheet](revision-cheatsheet.md) |

---

[:material-arrow-left: Interview overview](index.md){ .md-button }
[Next: Microsoft :material-arrow-right:](microsoft.md){ .md-button .md-button--primary }
