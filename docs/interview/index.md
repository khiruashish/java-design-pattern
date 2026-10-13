---
tags:
  - Interview
---

# Interview Preparation

This section turns the handbook into an interview plan. Pattern pages already end with an **Interview Corner** (beginner, mid-level, senior and a coding challenge) and a **STAR story**. The pages here add **company-specific preparation**, **low-level design (LLD) problems** with full code, **system design patterns**, and the **top 100 questions** in one place.

!!! warning "About company pages"
    Interview processes differ by team, level, country and year, and companies change them often. The company pages describe **commonly reported formats** and how design-pattern knowledge is typically tested there; they aren't official descriptions. Always check the company's own careers pages and ask your recruiter what your loop looks like.

## What you'll find here

<div class="grid cards" markdown>

-   :material-office-building:{ .lg .middle } **Company guides**

    ---

    Google, Microsoft, Amazon, SAP, and service companies (TCS, Infosys, Cognizant, Wipro, Accenture, Capgemini and others): format, what's evaluated, 15 practice questions each, and a 4-week plan.

    [:octicons-arrow-right-24: Google](google.md) · [Microsoft](microsoft.md) · [Amazon](amazon.md) · [SAP](sap.md) · [Service companies](service-companies.md)

-   :material-puzzle:{ .lg .middle } **LLD problems**

    ---

    12 classic machine-coding problems (Parking Lot, Elevator, Vending Machine, LRU Cache, Splitwise, BookMyShow-style booking and more), each with requirements, patterns, class diagram, runnable code and output.

    [:octicons-arrow-right-24: LLD problems](lld-problems.md)

-   :material-sitemap:{ .lg .middle } **System design patterns**

    ---

    The building blocks (caching, sharding, consistent hashing, queues, replication) and how microservice patterns combine in classic designs.

    [:octicons-arrow-right-24: System design patterns](system-design-patterns.md)

-   :material-format-list-numbered:{ .lg .middle } **Top 100 questions**

    ---

    The most frequently asked design-pattern, architecture and microservices questions, with short answers and links to the full explanations.

    [:octicons-arrow-right-24: Top 100](top-100-questions.md)

</div>

## Which patterns matter at which level

| Level | Expectation | Must know | Should know |
|---|---|---|---|
| **Fresher / 0–2 years** | Explain and code the classics; SOLID | Singleton, Factory, Builder, Strategy, Observer, Adapter, Decorator, Template Method | Facade, Proxy, Command, State, Iterator |
| **Mid / 3–7 years** | Choose patterns for LLD problems; Spring internals; basic microservices | All GoF essentials + Chain of Responsibility, Composite, State, Producer-Consumer, Thread Pool | Circuit Breaker, Saga, Outbox, API Gateway, CQRS |
| **Senior / 8–15 years** | Trade-offs, architecture, distributed systems, leading design reviews | Hexagonal/Clean, modular monolith, all resilience and data-consistency patterns, observability | Event sourcing, service mesh, migration strategies, anti-patterns and their cost |

## A four-week plan (any company)

| Week | Focus | Use these pages |
|---|---|---|
| 1 | OOP, SOLID, creational and structural patterns; code each example yourself | [OOP & SOLID](../getting-started/oop-solid.md), [Creational](../creational/index.md), [Structural](../structural/index.md) |
| 2 | Behavioral and concurrency patterns; 4 LLD problems under time pressure (45 min each) | [Behavioral](../behavioral/index.md), [Concurrency](../concurrency/index.md), [LLD problems](lld-problems.md) |
| 3 | Microservices and architecture; 2 system design mocks | [Microservices](../microservices/index.md), [Architecture](../architecture/index.md), [System design patterns](system-design-patterns.md) |
| 4 | Company-specific prep, STAR stories, top 100 questions, revision | Company page, [Top 100](top-100-questions.md), [Cheat sheet](revision-cheatsheet.md) |

!!! tip "In every design interview"
    1. **Clarify** requirements and constraints before drawing anything.
    2. **Name** the pattern only after describing the problem it solves ("the pricing rules change per campaign, so I'll isolate them behind a strategy").
    3. **State the trade-off** (what it costs) and an alternative you rejected.
    4. **Write code that runs**: small, tested pieces beat a big diagram.

---

[:material-arrow-left: Glossary](../glossary.md){ .md-button }
[Next: Google :material-arrow-right:](google.md){ .md-button .md-button--primary }
