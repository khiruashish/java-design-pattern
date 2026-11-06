# 6. Decorator: Retry + Cache + Log

<span class="level l2">L2</span> ★★☆ · Wrapping, ordering of decorators, composition over inheritance

## Problem

> A remote `PriceService` is slow and sometimes times out. Add **retries**, **caching** and **logging** without changing it,
> and without a subclass for every combination (`RetryingCachingLoggingPriceService`...).

## See the pattern in motion

Before you write any code, watch the pattern this problem is built on. Switch to **Without it** to see the bug the pattern prevents. [Read the full Decorator page](../structural/decorator.md).

<iframe class="motion-embed" src="../../motion/index.html?p=decorator&amp;embed=1" title="Animated walkthrough of the Decorator pattern" loading="lazy"></iframe>

## Key idea

Each concern is a class that **implements the same interface and wraps another instance of it**. Stack them like layers.
**Order matters**: logging outside caching logs every call, while caching outside retrying means a cached price never
retries. See [Decorator](../structural/decorator.md).

```mermaid
flowchart LR
    C[client] --> L[Logging] --> CA[Caching] --> R["Retrying (3)"] --> RS[RemotePriceService]
    RS -. timeout .-> R
    R -. retry .-> RS
```

## Solution

```java title="DecoratorPipeline.java"
--8<-- "coding-interview/src/DecoratorPipeline.java"
```

## Walkthrough

| Code | Why |
|---|---|
| `abstract static class PriceDecorator implements PriceService { protected final PriceService inner; }` | The base decorator holds the wrapped component. Each concrete decorator overrides only what it adds. |
| `for (int i = 1; i <= attempts; i++) try { return inner... } catch (...) { last = e; }` | Retries rethrow the **last** failure, so the caller sees the real cause. In production, add exponential backoff and retry only on transient errors. |
| `cache.computeIfAbsent(sku, inner::priceCents)` | If `inner` throws, nothing is cached and the exception propagates, so a failure is never cached by mistake. |
| `new Logging(new Caching(new Retrying(remote, 3)), log)` | The composition **is** the configuration. Reorder the constructor calls to change the behaviour. |

## Complexity

| Approach | Time | Space |
|---|---|---|
| cached call | O(1) | O(distinct SKUs) |
| uncached call | O(attempts) remote calls | - |

## Run it

```bash
java coding-interview/src/DecoratorPipeline.java      # JDK 21+ single-file launcher, no build needed
```

```text
--8<-- "coding-interview/outputs/DecoratorPipeline.txt"
```

## Common mistakes

* Subclassing for every combination, which leads to a class explosion.
* Putting the cache *inside* the retry, so every retry hits the cache miss path again, or caching exceptions.
* Forgetting that a decorator must forward **every** interface method. Large interfaces make that painful, so consider a dynamic proxy (problem 11).

## Follow-up questions

??? question "Decorator vs Proxy vs Adapter?"
    They have similar structure but different intent. A Decorator **adds behaviour** and can be stacked. A Proxy **controls access** (lazy loading, caching, remote access, security) and usually keeps the same behaviour. An Adapter **changes the interface**.

??? question "Where do you see decorators in the JDK and Spring?"
    `BufferedInputStream(new FileInputStream(...))`, `Collections.unmodifiableList(...)`, Spring's `TransactionAwareDataSourceProxy`, and Resilience4j's `Decorators.ofSupplier(...).withRetry(...).withCircuitBreaker(...)`.
