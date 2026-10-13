---
tags:
  - Interview
---

# SAP: Design Patterns in Interviews

!!! warning "Commonly reported, not official"
    Based on publicly shared candidate experiences. SAP is large (product teams, SAP Labs locations, cloud and on-premise products, consulting), and each unit interviews differently. Confirm with your recruiter.

## What the process commonly looks like

- An **online test** or screening (for early-career roles), followed by **technical interviews** and a **managerial/HR** conversation.
- Java developer roles commonly cover **core Java**, **OOP/design patterns**, **Spring Boot**, **REST**, **SQL**, and increasingly **cloud-native** topics (microservices, Kubernetes, multi-tenancy).
- Teams working on SAP Business Technology Platform (BTP) or extensions may discuss SAP-specific technologies such as the **SAP Cloud Application Programming Model (CAP)**, which supports Java and Node.js, or integration with **S/4HANA** via APIs and events. Knowing the general patterns behind them (multi-tenancy, extensibility, events, ACLs) matters more than product trivia.
- Experienced candidates are often asked about **enterprise concerns**: data consistency, extensibility, backward compatibility, security, and long product lifecycles.

## Where design patterns show up

| Situation | Pattern knowledge that helps |
|---|---|
| Enterprise application design | [Layered](../architecture/layered.md), [Hexagonal](../architecture/hexagonal.md), [Clean](../architecture/clean.md), [Modular Monolith](../architecture/modular-monolith.md) |
| Extensibility for customers/partners | [Strategy](../behavioral/strategy.md), [Template Method](../behavioral/template-method.md) (hooks), [Observer](../behavioral/observer.md)/events, plugin [Factory](../creational/factory-method.md) registries |
| Integrating with ERP or legacy systems | [Anti-Corruption Layer](../microservices/anti-corruption-layer.md), [Adapter](../structural/adapter.md), [Facade](../structural/facade.md), [Strangler Fig](../microservices/strangler-fig.md) |
| Multi-tenant SaaS | Tenant routing ([API Gateway](../microservices/api-gateway.md)), [Rate Limiter](../microservices/rate-limiter.md), config per tenant, data isolation |
| Business events | [Event-Driven](../architecture/event-driven.md), [Outbox](../microservices/transactional-outbox.md), [Saga](../microservices/saga.md), [Event Sourcing](../microservices/event-sourcing.md) |

## What interviewers look for

| Signal | What strong looks like |
|---|---|
| Java & Spring depth | Collections, concurrency, JVM basics; Spring DI, transactions, proxies; JPA pitfalls |
| Design | Clean responsibilities, extensibility without hacks, backward compatibility awareness |
| Enterprise thinking | Data consistency, auditability, security, multi-tenancy, upgrades over many years |
| Collaboration | Working across global teams, documenting decisions, customer orientation |

## 15 practice questions (with model answers)

??? question "1. How would you let customers add custom validation rules to an order process without changing core code?"
    Extension points: a `ValidationRule` [Strategy](../behavioral/strategy.md) interface registered per tenant, executed as a [Chain](../behavioral/chain-of-responsibility.md); events for side effects; sandboxing and timeouts for customer code.

??? question "2. Explain how Spring's `@Transactional` works and when it doesn't."
    A [Proxy](../structural/proxy.md) wraps the bean and starts/commits transactions; self-invocation bypasses the proxy, private methods aren't proxied, and checked exceptions don't roll back by default.

??? question "3. Design integration between a new cloud service and an existing ERP system."
    [ACL](../microservices/anti-corruption-layer.md) translating ERP data and codes; APIs for queries, events for changes; [Outbox](../microservices/transactional-outbox.md) for reliable publishing; idempotent consumers; reconciliation jobs.

??? question "4. How would you design a multi-tenant SaaS application?"
    Tenant resolution at the gateway, tenant context propagated, isolation model (shared schema with tenant ID, schema per tenant, or DB per tenant), per-tenant config and limits, noisy-neighbour protection, tenant-aware monitoring.

??? question "5. Template Method vs. Strategy for extensibility?"
    Template Method fixes the algorithm skeleton with overridable hooks (inheritance); Strategy swaps whole behaviors via composition. Prefer Strategy for customer extensions. See [Comparisons](../comparisons.md).

??? question "6. How do you evolve a public API used by thousands of customers?"
    Additive, backward-compatible changes; versioning for breaking changes; deprecation periods; consumer-driven contract tests; tolerant readers. See [BFF](../microservices/backend-for-frontend.md) (versioned partner contracts).

??? question "7. A business process spans finance, logistics and sales services. How do you keep data consistent?"
    [Saga](../microservices/saga.md) (orchestrated for core processes), [Outbox](../microservices/transactional-outbox.md), idempotency, semantic locks (`PENDING` states), and audit trails.

??? question "8. Where would you use Event Sourcing in an enterprise system?"
    Ledgers, approvals and workflows with strict audit needs; not for master data. Discuss snapshots, upcasting and GDPR (crypto-shredding). See [Event Sourcing](../microservices/event-sourcing.md).

??? question "9. Explain the Adapter and Facade patterns with an integration example."
    Adapter converts a legacy interface to the one you need; Facade gives a simple entry point over many calls (e.g. one `createSalesOrder()` hiding five BAPI-style calls).

??? question "10. How would you make a long-running batch job restartable?"
    Chunked processing with checkpoints (Spring Batch), idempotent writes, job state persisted, [Command](../behavioral/command.md)-like steps, and a dead-letter list for failed records.

??? question "11. How do you handle configuration and secrets across many landscapes (dev, test, prod)?"
    Externalized config per environment, typed validation, secrets in a vault/credential store, no secrets in code. See [Config Server](../microservices/config-server.md).

??? question "12. What is CQRS and when is it useful in enterprise apps?"
    Separate write models (rules) from read models (reports, dashboards); useful for heavy reporting or cross-module views. See [CQRS](../microservices/cqrs.md).

??? question "13. How do you migrate a monolithic on-premise module to the cloud incrementally?"
    [Strangler Fig](../microservices/strangler-fig.md): facade, extract capabilities one by one, ACL for remaining legacy data, parallel runs, retire old code.

??? question "14. How do you ensure a design stays maintainable over a 10-year product lifecycle?"
    Clear module boundaries, dependency rules enforced by tests (ArchUnit), documented decisions (ADRs), stable public APIs, and avoiding framework lock-in in the core ([Clean](../architecture/clean.md)).

??? question "15. Tell me about working with a distributed team across time zones."
    STAR: communication practices (written design docs, async reviews), a concrete conflict or misunderstanding, and how you resolved it.

## 4-week plan

| Week | Do |
|---|---|
| 1 | Core Java + OOP/SOLID; creational and structural patterns; Spring proxies and transactions |
| 2 | Behavioral patterns with extensibility focus (Strategy, Template Method, Observer, Chain); 3 LLD problems |
| 3 | Architecture (layered, hexagonal, clean, modular monolith) and integration patterns (ACL, outbox, saga, CQRS) |
| 4 | Multi-tenancy and API evolution scenarios; STAR stories; mock interviews |

---

[:material-arrow-left: Amazon](amazon.md){ .md-button }
[Next: Service companies :material-arrow-right:](service-companies.md){ .md-button .md-button--primary }
