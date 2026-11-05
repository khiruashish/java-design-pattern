# Design Patterns (GoF) — standalone code

A trimmed-down, self-contained copy of the handbook code that keeps **only the classic design patterns**:
the 26 creational, structural and behavioral patterns. It has no dependency on the rest of the repository,
so you can copy this folder anywhere and build it on its own.

Left out on purpose (still available under `../code/`): concurrency, microservices, architecture,
low-level-design problems, the FreshCart capstone, the Spring reference app and the OOP/SOLID principles.

## What's inside

| Module | Patterns |
|---|---|
| `creational/` (6) | Singleton, Factory Method, Abstract Factory, Builder, Prototype, Object Pool |
| `structural/` (7) | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| `behavioral/` (13) | Chain of Responsibility, Command, Interpreter, Iterator, Mediator, Memento, Null Object, Observer, Specification, State, Strategy, Template Method, Visitor |

Each pattern lives in `src/main/java/com/handbook/<module>/<pattern>/` with several real-world examples
(one sub-package per example, each with a runnable `*Demo` class) and unit tests in `src/test/java`.
`outputs/` holds the exact console output of every demo.

```
design-patterns/
├── pom.xml            # parent POM: creational, structural, behavioral
├── run-demos.sh       # runs every *Demo, writes or checks outputs/
├── creational/
├── structural/
├── behavioral/
└── outputs/           # expected demo output, mirrored by package
```

## Build and run

Requires **Java 21** and **Maven 3.9+**.

```bash
cd design-patterns
mvn test                     # 127 unit tests
./run-demos.sh               # runs every *Demo class, regenerates outputs/
./run-demos.sh --check       # fails if any demo output differs from outputs/
```

Run a single example:

```bash
cd design-patterns
mvn -q -pl creational compile
java -cp creational/target/classes com.handbook.creational.singleton.grocery.GroceryDemo
```
