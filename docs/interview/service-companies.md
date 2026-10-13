---
tags:
  - Interview
---

# Service Companies: TCS, Infosys, Cognizant, Wipro, Accenture, Capgemini and Others

!!! warning "Commonly reported, not official"
    Based on publicly shared candidate experiences. Hiring drives, tests and rounds change by year, role, campus vs. lateral hiring, and client project. Check the company's careers portal and your recruiter's instructions.

## What the process commonly looks like

**Freshers (campus and off-campus drives)**

- An **online test**: aptitude (quantitative, logical, verbal) plus a **coding** section; some companies use national hiring tests or named programmes for different salary bands.
- A **technical interview**: core Java/OOP, data structures, SQL, a few design patterns (Singleton, Factory, MVC), and your final-year project.
- An **HR interview**: communication, relocation, shifts, career goals.

**Experienced (lateral hiring, 2–15 years)**

- One or two **technical rounds** focused on the job description: Java 8–21 features, Spring Boot, REST, JPA/Hibernate, microservices, SQL, sometimes cloud (AWS/Azure) and DevOps basics.
- Often a **client or project round** for a specific account, testing practical problem solving and communication.
- A **managerial / HR** round.

## Where design patterns show up

| Question style | Typical expectation |
|---|---|
| "What design patterns have you used?" | Name 3–5 with **where** and **why** in your project (Singleton for config, Factory for payment types, Builder for DTOs, Strategy for rules, Observer/events) |
| "Write a thread-safe Singleton" | Double-checked locking with `volatile`, holder idiom, or enum; explain each. See [Singleton](../creational/singleton.md) |
| "Patterns used in Spring?" | Singleton scope, Factory (`BeanFactory`), Proxy (AOP, `@Transactional`), Template (`JdbcTemplate`), Observer (`ApplicationEvent`), Front Controller (`DispatcherServlet`) |
| "Microservices patterns you've used" | API Gateway, Service Discovery (Eureka), Config Server, Circuit Breaker (Resilience4j), Saga basics, distributed tracing |
| "Explain MVC" | Model/View/Controller in Spring MVC with `DispatcherServlet`, controllers, view resolvers |

## What interviewers look for

| Signal | What strong looks like |
|---|---|
| Fundamentals | Clear, correct definitions with a small code example |
| Practical experience | Concrete examples from your projects, not textbook lists |
| Spring Boot/microservices (lateral) | How things work under the hood (auto-configuration, proxies, transactions) and what you configured |
| Communication | Structured answers, honest about what you don't know |
| Client readiness | Explains trade-offs simply; asks clarifying questions |

## 15 practice questions (with model answers)

??? question "1. What is a design pattern? Name the three GoF categories."
    A reusable solution to a common design problem. Creational (object creation), Structural (composition of classes/objects), Behavioral (communication and responsibilities).

??? question "2. Write a thread-safe Singleton in Java."
    Enum singleton (`enum Config { INSTANCE; }`), or the holder idiom (`private static class Holder { static final Config I = new Config(); }`), or double-checked locking with a `volatile` field. See [Singleton](../creational/singleton.md).

??? question "3. How can a Singleton be broken? How do you prevent it?"
    Reflection, serialization, cloning and multiple class loaders. Use an enum; or guard the constructor, implement `readResolve`, and don't implement `Cloneable`.

??? question "4. Factory vs. Abstract Factory?"
    Factory creates one product type (choosing the subclass); Abstract Factory creates **families** of related products. See [Factory Method](../creational/factory-method.md) and [Abstract Factory](../creational/abstract-factory.md).

??? question "5. Which design patterns does Spring use?"
    Singleton (default bean scope), Factory (`BeanFactory`, `FactoryBean`), Proxy (AOP, `@Transactional`), Template Method (`JdbcTemplate`, `RestTemplate`), Observer (`ApplicationEventPublisher`), Adapter (`HandlerAdapter`), Front Controller (`DispatcherServlet`), Strategy (resource loading, `PlatformTransactionManager`).

??? question "6. Explain the Builder pattern. Where have you used it?"
    Step-by-step construction of complex objects, avoiding telescoping constructors; e.g. Lombok `@Builder` for DTOs, `HttpRequest.newBuilder()`. See [Builder](../creational/builder.md).

??? question "7. What is the Observer pattern? Real example?"
    Subjects notify registered observers on change; e.g. Spring `@EventListener` for `OrderPlacedEvent` sending emails. See [Observer](../behavioral/observer.md).

??? question "8. Strategy pattern with an example."
    Interchangeable algorithms behind an interface: `PaymentStrategy` with `UpiPayment`, `CardPayment`; chosen at runtime. See [Strategy](../behavioral/strategy.md).

??? question "9. What is MVC and how does Spring MVC implement it?"
    `DispatcherServlet` (front controller) routes to `@Controller` methods (controller), which return model data and a view name (or a body for REST); views render the model.

??? question "10. What are microservices? Name 5 patterns you've used."
    Small, independently deployable services around business capabilities. Patterns: [API Gateway](../microservices/api-gateway.md), [Service Discovery](../microservices/service-discovery.md), [Config Server](../microservices/config-server.md), [Circuit Breaker](../microservices/circuit-breaker.md), [Distributed Tracing](../microservices/distributed-tracing.md).

??? question "11. How does a circuit breaker work in Spring Boot?"
    Resilience4j `@CircuitBreaker(name, fallbackMethod)`: CLOSED → OPEN after failure-rate threshold → HALF_OPEN after wait → CLOSED if trial calls succeed. See [Circuit Breaker](../microservices/circuit-breaker.md).

??? question "12. How do you handle transactions across microservices?"
    Not with a single DB transaction: use the [Saga](../microservices/saga.md) pattern (orchestration or choreography) with compensating actions, plus the [outbox](../microservices/transactional-outbox.md) for reliable events.

??? question "13. Difference between Adapter and Facade?"
    Adapter makes an incompatible interface fit another; Facade provides a simpler interface over a complex subsystem. See [Comparisons](../comparisons.md).

??? question "14. What is SOLID? Explain one principle with code."
    Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion. E.g. Open/closed: add a new `DiscountRule` class without editing the checkout. See [OOP & SOLID](../getting-started/oop-solid.md).

??? question "15. Explain your project's architecture."
    Prepare a 2-minute answer: business purpose, main modules/services, tech stack, how a request flows, data storage, one design decision you made and why, and one problem you solved. Draw it if allowed.

## Preparation tips

- **Know your own project deeply**: architecture, your module, patterns used and why, a bug you fixed, how you deployed it.
- **Code by hand**: Singleton (3 ways), Factory, Builder, Strategy, Observer, Producer-Consumer, LRU cache.
- **Spring internals**: bean lifecycle, scopes, `@Transactional` proxy pitfalls, auto-configuration, profiles.
- **SQL**: joins, group by/having, indexes, N+1 problem in JPA.
- **Communication**: answer in a structure (definition → example → where you used it → trade-off).

## 4-week plan

| Week | Do |
|---|---|
| 1 | Aptitude practice (freshers) or Java 8–21 features (lateral); OOP & SOLID; Singleton, Factory, Builder |
| 2 | Strategy, Observer, Decorator, Adapter, Facade, Template Method, MVC; Spring patterns |
| 3 | Spring Boot + microservices patterns: gateway, discovery, config, circuit breaker, saga, tracing |
| 4 | Project explanation, mock technical + HR interviews, [Top 100 questions](top-100-questions.md) |

---

[:material-arrow-left: SAP](sap.md){ .md-button }
[Next: LLD problems :material-arrow-right:](lld-problems.md){ .md-button .md-button--primary }
