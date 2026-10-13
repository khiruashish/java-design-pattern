# Java Design Patterns Handbook

[![Handbook](https://github.com/khiruashish/java-design-pattern/actions/workflows/handbook.yml/badge.svg)](https://github.com/khiruashish/java-design-pattern/actions/workflows/handbook.yml)
![Java 21](https://img.shields.io/badge/Java-21-orange)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.3-6db33f)
![MkDocs Material](https://img.shields.io/badge/docs-MkDocs%20Material-526cfe)

A complete, runnable handbook of **56 design patterns in Java 21**, from GoF classics to microservices and architecture, written for three audiences at once:

- **Beginners** learning patterns for the first time,
- **Experienced engineers (up to 15 years)** who want trade-offs, production checklists and war stories,
- **Interview candidates** preparing for Google, Microsoft, Amazon, SAP and service companies (TCS, Infosys, Cognizant, Wipro, Accenture, Capgemini).

Every pattern page has **three real-world examples from different industries** (grocery, banking, travel, healthcare, streaming, ride-hailing, insurance, logistics), each with full Java code, its **exact output**, and unit tests. The site embeds the code and outputs straight from `code/` at build time, and CI fails if any output no longer matches the code.

## What's inside

| Section | Content |
|---|---|
| **Getting Started** | How to use the handbook, OOP & SOLID refresher, UML cheat sheet |
| **Creational (6)** | Singleton, Factory Method, Abstract Factory, Builder, Prototype, Object Pool |
| **Structural (7)** | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| **Behavioral (13)** | Chain of Responsibility, Command, Interpreter, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor, Null Object, Specification |
| **Concurrency (7)** | Producer-Consumer, Thread Pool, Future/Promise, Read-Write Lock, Immutable Object, Double-Checked Locking, Virtual Threads |
| **Microservices (18)** | Decomposition (DDD), API Gateway, BFF, Service Discovery, Circuit Breaker, Retry/Timeout/Bulkhead, Rate Limiter, Saga, Transactional Outbox, CQRS, Event Sourcing, Database per Service, Strangler Fig, Sidecar/Service Mesh, Idempotent Consumer, Distributed Tracing, Config Server, Anti-Corruption Layer |
| **Architecture (5)** | Layered, Hexagonal, Clean, Event-Driven, Modular Monolith (rules enforced with ArchUnit) |
| **Guides** | Pattern selection guide, pattern comparisons, anti-patterns, glossary |
| **Interview** | Company guides (Google, Microsoft, Amazon, SAP, service companies), 12 LLD problems with code, system design patterns, top 100 questions, revision cheat sheet |
| **Capstone** | **FreshCart**, a runnable grocery platform combining 22 patterns, including failure scenarios |

Each pattern page follows the same structure: TL;DR, intent, analogy, structure diagram, naive approach, three examples (code + output + tests), variations, framework usage, pros/cons, pitfalls, senior deep dive with a war story, comparisons, an interview corner (beginner / mid / senior / coding challenge), a STAR story and a quick revision card.

## Quick start

### Read the site locally

```bash
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
mkdocs serve                 # open http://127.0.0.1:8000
```

### Run the Java code

Requires **Java 21** and **Maven 3.9+**.

```bash
cd code
mvn test                     # 217 unit and ArchUnit tests
./run-demos.sh               # runs every *Demo class, writes outputs/
./run-demos.sh --check       # verifies outputs match the committed files (what CI runs)
```

Run a single example:

```bash
cd code
mvn -q -pl creational compile
java -cp creational/target/classes com.handbook.creational.singleton.grocery.GroceryDemo
```

Run the capstone:

```bash
cd code
mvn -q -pl capstone compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
java -cp "capstone/target/classes:$(cat capstone/target/classpath.txt)" com.handbook.capstone.FreshCartDemo
```

## Repository layout

```
.
├── MASTER_PROMPT.md          # the prompt the handbook was generated from
├── mkdocs.yml                # theme, navigation, markdown extensions
├── requirements.txt          # MkDocs Material + plugins
├── docs/                     # the handbook pages (Markdown)
├── code/                     # Maven multi-module project
│   ├── creational/ structural/ behavioral/ concurrency/
│   ├── microservices/        # distributed patterns as runnable simulations (H2, FakeClock)
│   ├── architecture/         # architecture styles + ArchUnit tests
│   ├── lld/                  # 12 interview LLD problems + consistent hashing
│   ├── capstone/             # FreshCart grocery platform
│   ├── spring-reference/     # Spring Boot snippets (compiled in CI, not run)
│   ├── outputs/              # exact console output of every demo, embedded in the docs
│   └── run-demos.sh
└── .github/workflows/        # CI: tests, output check, strict site build, GitHub Pages deploy
```

## How the microservices code runs

Distributed patterns are shown as **plain-Java simulations** so every example runs in milliseconds without Docker, Kafka or Kubernetes: time comes from a controllable `FakeClock`, randomness is seeded, brokers are in-memory, and databases are real **H2** in-memory databases with real JDBC transactions. Spring Boot versions of key pieces (Resilience4j, Spring Kafka, the outbox relay, `@ConfigurationProperties`, Micrometer Tracing) live in `code/spring-reference`; CI compiles that module but doesn't run it, because it needs real infrastructure. YAML snippets for Spring Cloud Gateway, Istio and similar tools are marked as references.

## Publishing the site

The GitHub Actions workflow runs the tests, checks the demo outputs, builds the site with `mkdocs build --strict`, and on pushes to `main` deploys it to the `gh-pages` branch. To make it public, enable **Settings → Pages → Deploy from a branch → `gh-pages`**.

## Tech stack

Java 21 (records, sealed types, pattern matching, virtual threads) · Maven · JUnit 5 · AssertJ · ArchUnit · H2 · Spring Boot 3.3 (reference module) · MkDocs Material · Mermaid diagrams

## Notes

Company and product names used in examples (PayFast, QuickDrop, Monsoon Express and others) are fictional. The company interview guides describe commonly reported formats, not official processes; check each company's careers pages.
