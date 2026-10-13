# Creational Patterns

Creational patterns answer one question: **who creates an object, when, and how?**
They separate *using* an object from *constructing* it, so the rest of your code doesn't depend on concrete classes or complicated set-up steps.

| Pattern | Problem it solves | Interview frequency | Status |
|---|---|---|---|
| [Singleton](singleton.md) | Exactly one shared instance (config, ID generator, registry) | ⭐⭐⭐⭐⭐ | ✅ |
| [Factory Method](factory-method.md) | Let subclasses or a method decide which class to instantiate | ⭐⭐⭐⭐⭐ | ✅ |
| [Abstract Factory](abstract-factory.md) | Create *families* of related objects that work together | ⭐⭐⭐⭐ | ✅ |
| [Builder](builder.md) | Build complex or immutable objects step by step | ⭐⭐⭐⭐⭐ | ✅ |
| [Prototype](prototype.md) | Create new objects by copying a configured one | ⭐⭐⭐ | ✅ |
| [Object Pool](object-pool.md) | Reuse expensive objects (connections, buffers) | ⭐⭐⭐ | ✅ |

```mermaid
flowchart TD
    Q1{Need exactly one<br/>shared instance?} -->|Yes| S[Singleton]
    Q1 -->|No| Q2{Object has many optional<br/>parameters or must be immutable?}
    Q2 -->|Yes| B[Builder]
    Q2 -->|No| Q3{Which concrete class<br/>is decided at runtime?}
    Q3 -->|One product| F[Factory Method]
    Q3 -->|A family of products| AF[Abstract Factory]
    Q3 -->|No| Q4{Creating is expensive and<br/>a copy would do?}
    Q4 -->|Copy a template| P[Prototype]
    Q4 -->|Reuse instances| OP[Object Pool]
```
