---
tags:
  - Interview
---

# Amazon: Design Patterns in Interviews

!!! warning "Commonly reported, not official"
    Based on Amazon's public interview guidance and widely shared candidate experiences. Details vary by team and level. Confirm with your recruiter.

## What the process commonly looks like

- An **online assessment** (coding, sometimes work-style questions) or phone screen, then an **interview loop** (often 4–5 interviews).
- Rounds cover **coding**, **object-oriented / low-level design** (common for SDE I/II), **system design** (SDE II and above), and **behavioral** questions in every round.
- Amazon publishes its **Leadership Principles** (Customer Obsession, Ownership, Dive Deep, Bias for Action, and others) and its interview guidance says behavioral questions are based on them. One interviewer, often called the **Bar Raiser**, is from outside the hiring team.

## Where design patterns show up

| Situation | Pattern knowledge that helps |
|---|---|
| LLD: "design a locker system / parking lot / vending machine" | [State](../behavioral/state.md), [Strategy](../behavioral/strategy.md), [Factory Method](../creational/factory-method.md), [Singleton](../creational/singleton.md) (and its pitfalls) |
| Extensibility follow-ups ("now add a new payment type") | Open/closed with [Strategy](../behavioral/strategy.md)/[Factory](../creational/factory-method.md), [Decorator](../structural/decorator.md) |
| System design (orders, inventory, notifications) | [Saga](../microservices/saga.md), [Outbox](../microservices/transactional-outbox.md), [Idempotent Consumer](../microservices/idempotent-consumer.md), [CQRS](../microservices/cqrs.md), queues |
| Operational excellence | [Circuit Breaker](../microservices/circuit-breaker.md), [Bulkhead](../microservices/retry-timeout-bulkhead.md), [Rate Limiter](../microservices/rate-limiter.md), [Tracing](../microservices/distributed-tracing.md) |

## What interviewers look for

| Signal | What strong looks like |
|---|---|
| LLD | Clear entities and responsibilities, extensible where requirements will change, working code for the core flow |
| System design | Requirements and scale first, simple design that works, then bottlenecks, failure modes and cost |
| Leadership Principles | Specific STAR stories with **your** actions, data, and results; real trade-offs and mistakes |
| Ownership | Talks about operating what you build: monitoring, on-call, fixing root causes |

!!! tip "Prepare two stories per Leadership Principle"
    Interviewers ask follow-ups ("what data did you use?", "what would you do differently?"). Each STAR tie-in at the end of the pattern pages in this handbook is a template; replace it with your own experience.

## 15 practice questions (with model answers)

??? question "1. Design an Amazon Locker-style package locker system."
    Lockers with sizes (S/M/L), allocation [Strategy](../behavioral/strategy.md) (smallest fitting), codes with expiry, locker [State](../behavioral/state.md) (free/reserved/occupied), notifications via [Observer](../behavioral/observer.md); concurrency on allocation.

??? question "2. Design a vending machine."
    [State](../behavioral/state.md) machine: Idle → HasMoney → Dispensing; coin handling; out-of-stock; refunds. See the [Vending Machine LLD](lld-problems.md#3-vending-machine).

??? question "3. Your checkout calls payment, inventory and shipping. How do you keep them consistent?"
    Orchestrated [Saga](../microservices/saga.md) with compensations, [Outbox](../microservices/transactional-outbox.md) for events, idempotent handlers, timeouts and `PENDING` states.

??? question "4. How do you avoid charging a customer twice when the client retries?"
    `Idempotency-Key` stored with the response; unique constraints; replay the stored response. See [Idempotent Consumer](../microservices/idempotent-consumer.md).

??? question "5. Add a new discount type to a pricing engine without modifying existing code."
    Each discount is a [Strategy](../behavioral/strategy.md)/[Specification](../behavioral/specification.md); a registry or chain applies them; new type = new class + registration. See the [Discount Engine LLD](lld-problems.md#11-grocery-discount-engine).

??? question "6. Design a notification service for order updates (email, SMS, push)."
    Channel [Strategy](../behavioral/strategy.md) + [Factory](../creational/factory-method.md), templates ([Template Method](../behavioral/template-method.md)), preferences, retries and DLQ, dedupe keys, rate limits. See the [Notification LLD](lld-problems.md#9-notification-system).

??? question "7. Your service depends on a slow third-party API. How do you protect it?"
    Timeouts, [Circuit Breaker](../microservices/circuit-breaker.md) with fallback, [Bulkhead](../microservices/retry-timeout-bulkhead.md), caching, async processing via queues.

??? question "8. Design a system to show 'customers who bought this also bought'."
    Event stream of orders → batch/stream job computing co-occurrence → read model per product ([CQRS](../microservices/cqrs.md)) in a key-value store, cached at the edge.

??? question "9. Singleton: when is it acceptable and what are the risks?"
    For truly single, stateless or immutable resources (config, registries) via DI; risks are hidden global state, testing pain, and false assumptions of uniqueness across JVMs. See [Singleton](../creational/singleton.md).

??? question "10. Design an inventory service that never oversells during a flash sale."
    Atomic conditional decrements (`UPDATE ... SET qty = qty - 1 WHERE qty > 0`), reservations with TTL, a queue for the hottest SKUs, rate limits per user, and reconciliation.

??? question "11. How would you find the cause of rising p99 latency in production?"
    Metrics to find which endpoint, [tracing](../microservices/distributed-tracing.md) to find the slow hop, logs for that trace; check recent deploys and dependencies (Dive Deep).

??? question "12. Observer vs. pub/sub messaging?"
    Observer is in-process and synchronous by default; pub/sub uses a broker for decoupled, asynchronous, durable delivery across services. See [Event-Driven](../architecture/event-driven.md).

??? question "13. Design a parking garage pricing system that changes per hour, weekend and vehicle type."
    Composable pricing rules ([Strategy](../behavioral/strategy.md) + [Chain](../behavioral/chain-of-responsibility.md) or [Decorator](../structural/decorator.md)), config-driven rates, injectable clock for tests.

??? question "14. Tell me about a time you disagreed with your team's design (Have Backbone; Disagree and Commit)."
    STAR: your data-driven argument, how you disagreed respectfully, the decision, how you committed, and the outcome either way.

??? question "15. Tell me about a production issue you owned end to end (Ownership, Dive Deep)."
    STAR: detection, mitigation first, root cause with evidence, long-term fix (tests, alarms, runbooks), and what you changed in the team's process.

## 4-week plan

| Week | Do |
|---|---|
| 1 | Coding fundamentals; 2 LLD problems ([Parking Lot](lld-problems.md#1-parking-lot), [Vending Machine](lld-problems.md#3-vending-machine)) with working code |
| 2 | 4 more LLD problems; patterns: State, Strategy, Observer, Factory, Decorator |
| 3 | System design: orders, inventory, notifications; Saga, Outbox, Idempotency, Circuit Breaker |
| 4 | 2 STAR stories per Leadership Principle; mock loop with a "bar raiser" style interviewer |

---

[:material-arrow-left: Microsoft](microsoft.md){ .md-button }
[Next: SAP :material-arrow-right:](sap.md){ .md-button .md-button--primary }
