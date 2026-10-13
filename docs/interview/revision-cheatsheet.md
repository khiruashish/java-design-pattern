---
tags:
  - Interview
---

# Revision Cheat Sheet

One page to revise every pattern in the handbook the night before an interview. Each row: **intent**, the **key mechanism**, a **real JDK/Spring example**, and **one sentence to say**. Click a pattern name for the full page with three worked examples.

!!! tip "How to use this page"
    Cover the last column and try to say it yourself. If you can't explain a row in one breath, read that pattern's **Quick Revision Card** and **Interview Corner**.

---

## Creational

| Pattern | Intent | Key mechanism | Real example | Say this |
|---|---|---|---|---|
| [Singleton](../creational/singleton.md) | Exactly one instance | Private constructor; enum / holder / DCL + `volatile` | `Runtime.getRuntime()`; Spring singleton scope | "Enum or holder idiom; DCL needs `volatile`; it's one per class loader, never per cluster." |
| [Factory Method](../creational/factory-method.md) | Decide which class to create | Static factory, overridden `createX()`, or a registry | `List.of`, `Calendar.getInstance`, `Collection.iterator()` | "Callers depend on an interface; new types need no edits; no silent `default`." |
| [Abstract Factory](../creational/abstract-factory.md) | Create matching families | One method per product, one factory per family | `DocumentBuilderFactory`, Spring `@Configuration` + `@Profile` | "Easy to add a family, hard to add a product type." |
| [Builder](../creational/builder.md) | Assemble complex immutable objects | Fluent builder, step builder, director | `StringBuilder`, `HttpRequest.newBuilder()`, `Thread.ofVirtual()` | "Fixes telescoping constructors; cross-field validation in `build()`." |
| [Prototype](../creational/prototype.md) | Create by copying | Copy constructor / `copy()`; registry of templates | `new ArrayList<>(list)`, `JsonNode.deepCopy()` | "Deep-copy mutable fields, share immutable ones; avoid `clone()`." |
| [Object Pool](../creational/object-pool.md) | Reuse expensive, limited objects | Bounded queue, validate on borrow, reset on return | HikariCP, HTTP connection pools | "Acquire late, release early; size with Little's Law; never pool cheap objects." |

## Structural

| Pattern | Intent | Key mechanism | Real example | Say this |
|---|---|---|---|---|
| [Adapter](../structural/adapter.md) | Make an interface fit | Wrap the adaptee; translate calls, data, errors | `Arrays.asList`, `InputStreamReader`, Spring `HandlerAdapter` | "Changes the interface; translate units and codes; fail loudly on unknowns." |
| [Bridge](../structural/bridge.md) | Two dimensions varying independently | Abstraction holds an implementor | JDBC API + drivers, SLF4J + back-ends | "M + N classes instead of M × N; designed up front." |
| [Composite](../structural/composite.md) | Treat parts and wholes alike | Composite delegates to children and combines | Swing `Container`, DOM, Spring `Composite*` | "Uniform trees; watch cycles and deep recursion." |
| [Decorator](../structural/decorator.md) | Add behavior dynamically | Same interface, wraps one object, stackable | `BufferedInputStream`, Spring AOP | "Same interface, adds behavior; order matters." |
| [Facade](../structural/facade.md) | Simplify a subsystem | Task-level methods orchestrate many classes | `JdbcTemplate`, SLF4J, Spring `@Service` use cases | "One simple entry point; avoid god facades." |
| [Flyweight](../structural/flyweight.md) | Save memory by sharing | Intrinsic (shared, immutable) vs extrinsic (passed in) | `Integer.valueOf` cache, String pool | "Integer 127 == 127 but 128 != 128; bound the pool." |
| [Proxy](../structural/proxy.md) | Control access | Same interface; lazy, protection, remote, caching | `@Transactional`, Hibernate lazy loading, gRPC stubs | "Controls access vs Decorator adds behavior; self-invocation bypasses Spring proxies." |

## Behavioral

| Pattern | Intent | Key mechanism | Real example | Say this |
|---|---|---|---|---|
| [Chain of Responsibility](../behavioral/chain-of-responsibility.md) | Pass a request along handlers | Linked handlers or a list; may short-circuit | Servlet `FilterChain`, Spring Security filters | "Order is policy; make 'unhandled' explicit." |
| [Command](../behavioral/command.md) | Request as an object | `execute`/`undo`; invoker keeps history or a queue | `Runnable`, Swing `Action`, job queues | "Queue, log, retry, undo; commands need IDs and idempotency." |
| [Interpreter](../behavioral/interpreter.md) | Evaluate a small language | Parser → AST of terminal/non-terminal nodes | Regex, SpEL, JPQL, cron | "Parse once, evaluate many; keep grammars small; never eval untrusted code." |
| [Iterator](../behavioral/iterator.md) | Traverse without exposing structure | `hasNext`/`next`; lazy pagination, merging | `Collection.iterator()`, `ResultSet`, AWS paginators | "Fail-fast vs weakly consistent; k-way merge with a heap." |
| [Mediator](../behavioral/mediator.md) | Centralize peer coordination | Colleagues talk only to the mediator | `DispatcherServlet`, chat rooms, orchestrators | "Many-to-many becomes many-to-one; beware the god mediator." |
| [Memento](../behavioral/memento.md) | Snapshot and restore | Originator creates opaque snapshots; caretaker stores | Savepoints, Spring Batch `ExecutionContext` | "Snapshot undo vs Command's inverse undo; version persisted mementos." |
| [Observer](../behavioral/observer.md) | Notify dependents of changes | Subscribe / notify; unsubscribe handles | `@EventListener`, `PropertyChangeListener`, Kafka | "Isolate failures, avoid leaks, notify after commit." |
| [State](../behavioral/state.md) | Behavior depends on state | Context delegates; states return next state | Order/payment lifecycles, `Thread.State` | "States switch themselves; Strategy is chosen by the client." |
| [Strategy](../behavioral/strategy.md) | Swap algorithms | Interface or lambda injected into a context | `Comparator`, `PasswordEncoder` | "Composition, runtime choice; lambdas make it one line." |
| [Template Method](../behavioral/template-method.md) | Fixed skeleton, variable steps | `final` template + abstract steps + hooks | `HttpServlet.service`, `AbstractList`, `JdbcTemplate` | "Hollywood Principle; make the template `final`." |
| [Visitor](../behavioral/visitor.md) | Add operations to stable types | Double dispatch, or sealed + pattern matching | `FileVisitor`, compiler AST visitors | "Easy new operations, hard new types; avoid no-op defaults." |
| [Null Object](../behavioral/null-object.md) | Replace `null` with neutral behavior | Same interface, do-nothing implementation | `Collections.emptyList()`, NOP loggers | "Optional at boundaries, Null Object inside; never for security or payments." |
| [Specification](../behavioral/specification.md) | Composable business rules | `isSatisfiedBy` + and/or/not | Spring Data JPA `Specification` | "One definition of each rule; translate to SQL for big data." |

## Concurrency

| Pattern | Intent | Key mechanism | Real example | Say this |
|---|---|---|---|---|
| [Producer-Consumer](../concurrency/producer-consumer.md) | Decouple producers and consumers | Bounded `BlockingQueue`, poison pills | `ThreadPoolExecutor`, async log appenders, Kafka | "Always bound the queue; `while` around `wait()`." |
| [Thread Pool](../concurrency/thread-pool.md) | Reuse a bounded set of threads | Core → queue → max → rejection policy | `ThreadPoolExecutor`, Tomcat pools | "Explicit bounded queue; CPU-bound ≈ cores; beware same-pool deadlock." |
| [Future / Promise](../concurrency/future-promise.md) | Result available later; compose async work | `supplyAsync`, `thenCompose`, `allOf`, timeouts | `CompletableFuture`, `HttpClient.sendAsync` | "Always pass an executor for I/O; `thenCompose` is flatMap." |
| [Read-Write Lock](../concurrency/read-write-lock.md) | Shared reads, exclusive writes | `ReentrantReadWriteLock`, `StampedLock` | Caches, configuration | "Downgrade OK, upgrade deadlocks; no I/O under the write lock." |
| [Immutable Object](../concurrency/immutable-object.md) | Thread safety without locks | Final fields, defensive copies, withers | `String`, `java.time`, records | "Records are shallowly immutable: `List.copyOf` components." |
| [Double-Checked Locking](../concurrency/double-checked-locking.md) | Lazy init with minimal locking | Check, lock, check again; `volatile` | Guava `memoize`, `String.hashCode` (racy single-check) | "Without `volatile` you can see a half-built object." |
| [Virtual Threads](../concurrency/virtual-threads.md) | Millions of cheap blocking threads | JVM-scheduled, unmount on blocking | Spring Boot `spring.threads.virtual.enabled` | "I/O-bound only; limit resources, not threads; watch pinning." |

---

## SOLID in five lines

| Principle | One line | Patterns that embody it |
|---|---|---|
| **S**ingle Responsibility | One reason to change | Facade, Command, Strategy (one algorithm per class) |
| **O**pen/Closed | Extend without modifying | Strategy, Decorator, Observer, Factory, Visitor |
| **L**iskov Substitution | Subtypes must keep the parent's promises | Template Method (hooks must not break the template) |
| **I**nterface Segregation | Small, focused interfaces | Role interfaces, Adapter targets |
| **D**ependency Inversion | Depend on abstractions | Strategy, Abstract Factory, DI in general |

## The 15 sentences interviewers most like to hear

1. "I'd introduce this pattern when the **second variation** appears; until then a simple `if` is clearer."
2. "Strategy and State share a structure; the difference is **who switches** the behavior."
3. "Decorator **adds behavior**, Proxy **controls access**, Adapter **changes the interface**, Facade **simplifies**."
4. "Double-checked locking needs **`volatile`** to prevent publishing a partially constructed object."
5. "Spring's `@Transactional` is a **proxy**, so **self-invocation** bypasses it."
6. "Records are **shallowly immutable**; I copy mutable components in the compact constructor."
7. "Every queue needs a **bound** and a decision about what happens when it's full."
8. "Retries need **idempotency keys**; retrying a non-idempotent POST double-charges."
9. "Virtual threads remove the thread limit, so I make **resource limits explicit** with pools and semaphores."
10. "I never do **I/O while holding a lock**; I prepare outside and swap inside."
11. "A singleton is **per class loader / per JVM**; across pods I need a database sequence or leader election."
12. "Unknown codes and states should **fail loudly**, never fall into a silent `default`."
13. "In-process events must be published **after commit**, or through an **outbox** across services."
14. "For closed hierarchies in Java 21, **sealed types + pattern matching** give me Visitor's safety with less code."
15. "Patterns are **responses to forces**. Let me first name the force, then pick the pattern."

## Night-before checklist

- [ ] I can write a **thread-safe Singleton** three ways (enum, holder, DCL with `volatile`).
- [ ] I can write a **Builder** with validation in `build()` and explain step builders.
- [ ] I can implement a **Strategy** with lambdas and explain Strategy vs State.
- [ ] I can implement **Observer** with unsubscribe and failure isolation.
- [ ] I can explain **`ThreadPoolExecutor`** task flow and rejection policies.
- [ ] I can write a **bounded buffer** with `wait`/`notifyAll` or `BlockingQueue`.
- [ ] I can explain **Decorator vs Proxy vs Adapter vs Facade** in one breath.
- [ ] I can map patterns onto **Parking Lot, Elevator, Vending Machine** and **Notification System** designs (see the [Pattern Selection Guide](../pattern-selection-guide.md#4-patterns-behind-classic-interview-problems)).
- [ ] I have a **STAR story** for at least three patterns (see the end of each pattern page).

---

[:material-arrow-left: Top 100 questions](top-100-questions.md){ .md-button }
[Next: Capstone :material-arrow-right:](../projects/grocery-platform.md){ .md-button .md-button--primary }
