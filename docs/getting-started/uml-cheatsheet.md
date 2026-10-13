# UML Cheat Sheet

Every diagram in this handbook is written in [Mermaid](https://mermaid.js.org/), so it's plain text you can copy into your own notes, a GitHub README or Confluence.

## Class diagram relationships

```mermaid
classDiagram
    direction LR
    class Animal {
        <<abstract>>
        #String name
        +speak()* String
    }
    class Dog
    class Owner
    class Leash
    class Kennel
    class Comparable~T~ {
        <<interface>>
        +compareTo(T other) int
    }
    Animal <|-- Dog : inheritance
    Comparable <|.. Dog : implements
    Owner --> Dog : association
    Dog o-- Leash : aggregation
    Kennel *-- Animal : composition
    Owner ..> Leash : dependency
```

| Arrow | Name | Meaning | Java |
|---|---|---|---|
| `<\|--` | Inheritance | is-a | `extends` |
| `<\|..` | Realization | implements | `implements` |
| `-->` | Association | has a reference | field |
| `o--` | Aggregation | has-a; the part can live on its own | field passed in from outside |
| `*--` | Composition | owns; the part dies with the whole | field created inside |
| `..>` | Dependency | uses temporarily | method parameter or local variable |

**Visibility:** `+` public, `-` private, `#` protected, `~` package-private.
**Members:** `*` abstract, `$` static.

## Sequence diagram

Used throughout the microservices section.

```mermaid
sequenceDiagram
    autonumber
    actor U as Customer
    participant G as API Gateway
    participant O as Order Service
    participant P as Payment Service
    U->>G: POST /orders
    G->>O: createOrder()
    O->>P: charge(orderId, amount)
    alt payment ok
        P-->>O: CAPTURED
        O-->>G: 201 Created
    else payment failed
        P-->>O: DECLINED
        O-->>G: 402 Payment Required
    end
    G-->>U: response
```

## State diagram

Used for State, Saga and order lifecycles.

```mermaid
stateDiagram-v2
    [*] --> CREATED
    CREATED --> PAID : payment captured
    CREATED --> CANCELLED : payment failed
    PAID --> PACKED
    PACKED --> OUT_FOR_DELIVERY
    OUT_FOR_DELIVERY --> DELIVERED
    DELIVERED --> [*]
    CANCELLED --> [*]
```

!!! tip "In interviews"
    You don't need perfect UML. Interviewers want to see **classes, their responsibilities, and the relationships between them** (especially interface vs. implementation). Boxes and arrows on a whiteboard, labelled clearly, are enough.
