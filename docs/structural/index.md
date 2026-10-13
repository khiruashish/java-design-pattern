# Structural Patterns

Structural patterns answer: **how do classes and objects fit together into larger structures** while staying flexible?
They're about **composition**: wrapping, adapting, combining and sharing objects so that each piece stays simple.

| Pattern | Problem it solves | Interview frequency | Status |
|---|---|---|---|
| [Adapter](adapter.md) | Make an incompatible interface fit the one you need | ⭐⭐⭐⭐⭐ | ✅ |
| [Bridge](bridge.md) | Vary an abstraction and its implementation independently | ⭐⭐⭐ | ✅ |
| [Composite](composite.md) | Treat single objects and trees of objects the same way | ⭐⭐⭐⭐ | ✅ |
| [Decorator](decorator.md) | Add behavior to an object without subclassing | ⭐⭐⭐⭐⭐ | ✅ |
| [Facade](facade.md) | One simple entry point to a complex subsystem | ⭐⭐⭐⭐ | ✅ |
| [Flyweight](flyweight.md) | Share immutable state across many objects to save memory | ⭐⭐⭐ | ✅ |
| [Proxy](proxy.md) | Control access to an object (lazy, remote, cached, secured) | ⭐⭐⭐⭐⭐ | ✅ |

```mermaid
flowchart TD
    Q1{What do you need?} -->|Make an existing class fit an interface| A[Adapter]
    Q1 -->|Add responsibilities at runtime, stackable| D[Decorator]
    Q1 -->|Control access: lazy, cache, security, remote| P[Proxy]
    Q1 -->|Simplify a complex subsystem| F[Facade]
    Q1 -->|Part-whole trees| C[Composite]
    Q1 -->|Two dimensions that vary independently| B[Bridge]
    Q1 -->|Millions of similar objects| FW[Flyweight]
```

!!! tip "Adapter vs. Decorator vs. Proxy vs. Facade"
    All four wrap something. Ask **what changes**:
    **Adapter** changes the *interface*. **Decorator** keeps the interface and *adds behavior*. **Proxy** keeps the interface and *controls access*. **Facade** gives a *new, simpler* interface to *many* objects.
