---
tags:
  - Interview
---

# Microsoft: Design Patterns in Interviews

!!! warning "Commonly reported, not official"
    Based on publicly shared candidate experiences and Microsoft's general interview guidance. Teams run their own loops, and formats vary by level and organization. Confirm with your recruiter.

## What the process commonly looks like

- A **screening** call or online assessment, then a **loop** of several interviews (often 4–5) with different engineers and a hiring manager.
- Rounds mix **coding**, **design** (object-oriented design for mid-level, system design for senior), and **behavioral** questions about collaboration, growth mindset and customer focus.
- Teams building developer platforms (Azure, Office, Windows, GitHub, Xbox) often ask domain-flavoured design questions.

## Where design patterns show up

| Situation | Pattern knowledge that helps |
|---|---|
| "Design a class library / SDK" | [Builder](../creational/builder.md), [Factory Method](../creational/factory-method.md), [Facade](../structural/facade.md), immutability, versioning |
| OOD questions (parking lot, elevator, file system) | [State](../behavioral/state.md), [Strategy](../behavioral/strategy.md), [Composite](../structural/composite.md), [Observer](../behavioral/observer.md) |
| Cloud service design | [Retry/Timeout/Bulkhead](../microservices/retry-timeout-bulkhead.md), [Circuit Breaker](../microservices/circuit-breaker.md), [Config Server](../microservices/config-server.md), [Sidecar](../microservices/sidecar-service-mesh.md) |
| Productivity apps (editor, collaboration) | [Command](../behavioral/command.md) (undo), [Memento](../behavioral/memento.md), [Observer](../behavioral/observer.md), [Flyweight](../structural/flyweight.md) |

!!! info "Azure Architecture Center"
    Microsoft publishes a catalogue of cloud design patterns (Circuit Breaker, Retry, Bulkhead, Sidecar, Strangler Fig, CQRS, Event Sourcing, Gateway patterns and more). The microservices section of this handbook covers most of them with runnable code, and it's worth skimming Microsoft's descriptions before a loop.

## What interviewers look for

| Signal | What strong looks like |
|---|---|
| Problem solving & coding | Correct, efficient, readable; tests edge cases |
| Design | Clear classes and responsibilities, extensibility where it's needed, trade-offs stated |
| Collaboration | Explains reasoning, accepts feedback, asks good questions |
| Growth mindset | Talks about learning from mistakes and feedback |
| Customer focus | Connects design choices to user impact |

## 15 practice questions (with model answers)

??? question "1. Design a file system with folders and files that can compute total size."
    [Composite](../structural/composite.md): `Node` interface with `size()`; `Folder` sums children. Add a [Visitor](../behavioral/visitor.md) for operations like search or permissions reports.

??? question "2. Implement undo/redo for a drawing app."
    [Command](../behavioral/command.md) objects on undo/redo stacks; clear redo on new actions; group commands into macros; consider [Memento](../behavioral/memento.md) for complex objects.

??? question "3. Design a parking lot."
    Spot types via enum/factory, pricing [Strategy](../behavioral/strategy.md), ticket lifecycle, concurrency on spot allocation. See the [Parking Lot LLD](lld-problems.md#1-parking-lot).

??? question "4. How would you design an SDK client for a cloud storage service?"
    Builder for client configuration, immutable request objects, retries with backoff and jitter for transient errors, pluggable credentials (Strategy), pagination via Iterator, a Facade for common tasks.

??? question "5. Where would you use the Observer pattern in a collaborative document editor?"
    Views observing the document model; remote changes as events; batching updates to avoid UI storms; weak references or explicit unsubscription to avoid leaks.

??? question "6. Explain the Retry pattern. When can retrying make things worse?"
    Retry transient failures with backoff and jitter; it worsens outages when it multiplies load (retry storms) or duplicates non-idempotent operations. See [Retry / Timeout / Bulkhead](../microservices/retry-timeout-bulkhead.md).

??? question "7. Design an elevator controller for a 20-floor building with 4 elevators."
    [State](../behavioral/state.md) per elevator, a dispatching [Strategy](../behavioral/strategy.md) (nearest car, SCAN), requests queue, observers for displays. See the [Elevator LLD](lld-problems.md#2-elevator-system).

??? question "8. What's the difference between Decorator and Proxy?"
    Both wrap the same interface. Decorator **adds behavior** (stackable); Proxy **controls access** (lazy loading, security, remote). See [Comparisons](../comparisons.md).

??? question "9. How do you make a service's configuration changeable without redeploys?"
    Externalized config with refresh for flags/limits, typed validation, secrets in a vault, staged rollout. See [Config Server](../microservices/config-server.md).

??? question "10. Design a logging framework."
    Levels with [Chain of Responsibility](../behavioral/chain-of-responsibility.md) or filters, appenders as [Strategy](../behavioral/strategy.md), async appender with a bounded queue, formatter templates. See the [Logging LLD](lld-problems.md#10-logging-framework).

??? question "11. How would you migrate a large legacy service to the cloud without downtime?"
    [Strangler Fig](../microservices/strangler-fig.md) behind a gateway, [ACL](../microservices/anti-corruption-layer.md) for legacy data, gradual rollout with kill switches, parallel runs for calculations.

??? question "12. Explain the Sidecar pattern and give a use case."
    A helper process next to the app (same pod) for cross-cutting concerns: mTLS, telemetry, log shipping, secrets. See [Sidecar](../microservices/sidecar-service-mesh.md).

??? question "13. Why prefer composition over inheritance? Give an example."
    Inheritance couples subclasses to parent internals and explodes combinations; composition (Strategy/Decorator) varies behavior at runtime. E.g. `Notifier` with pluggable channels instead of `EmailSmsPushNotifier` subclasses.

??? question "14. Design a thread pool."
    Bounded work queue, worker threads, rejection policy, graceful shutdown, metrics; discuss sizing for CPU vs. I/O. See [Thread Pool](../concurrency/thread-pool.md).

??? question "15. Tell me about a time you received critical feedback on a design."
    STAR focused on listening, what you changed, and the outcome; Microsoft interviewers often probe for growth mindset.

## 4-week plan

| Week | Do |
|---|---|
| 1 | Coding fundamentals + OOP/SOLID; implement Composite, Command, Observer examples yourself |
| 2 | 6 OOD problems from [LLD problems](lld-problems.md) in 45 minutes each; review with the class diagrams |
| 3 | Cloud patterns: retry, circuit breaker, sidecar, strangler, CQRS; one system design mock |
| 4 | Behavioral stories (growth mindset, collaboration, customer impact); mock loop |

---

[:material-arrow-left: Google](google.md){ .md-button }
[Next: Amazon :material-arrow-right:](amazon.md){ .md-button .md-button--primary }
