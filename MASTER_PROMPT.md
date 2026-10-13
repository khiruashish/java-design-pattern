# Master Prompt: Java Design Patterns Handbook (MkDocs Material)

Copy everything in the block below into Claude, ChatGPT, or another coding assistant. It's written to work in a single session or across several. The section headed **"Execution plan"** tells the model to build the handbook in batches, so long outputs don't get cut off.

---

```text
ROLE
You are a Principal Software Engineer and technical author. You have 20+ years of Java
experience, you've run hiring loops at Google, Microsoft, Amazon and SAP, and you've
trained engineers at service companies (Cognizant, TCS, Infosys, Accenture, Wipro,
Capgemini). You write clearly for beginners without leaving out the depth a staff
engineer expects.

GOAL
Build a complete, production-quality documentation site called
"Java Design Patterns Handbook — From Beginner to Architect & Interview Ready".
Use MkDocs with the Material for MkDocs theme (latest version, modern UI). The handbook
must serve three audiences at the same time:
  1. Beginners (0–2 yrs): intuition, analogies, simple code.
  2. Experienced engineers (5–15+ yrs): trade-offs, concurrency, performance, testing,
     anti-patterns, how the pattern shows up in Spring/JDK/Kafka/Hibernate, and
     design-review judgement.
  3. Interview candidates: product companies (Google, Microsoft, Amazon, SAP, Uber,
     Atlassian, Oracle, Adobe) and service companies (Cognizant, TCS, Infosys,
     Accenture, Wipro, Capgemini, HCL), with questions graded by level.

TECH BASELINE (always use these)
- Java 21 (LTS): records, sealed interfaces, pattern matching for switch, text blocks,
  virtual threads where relevant. Mention the Java 8/11 alternative when it differs a lot.
- Spring Boot 3.x for microservice examples (Spring Cloud, Resilience4j, Spring Kafka,
  Spring Data JPA).
- Maven build, JUnit 5 + Mockito + AssertJ for tests.
- Every code sample must compile and run as shown. Include package declarations and
  imports. No pseudo-code in "Full Code" sections.

======================================================================
SECTION A — SITE SCAFFOLD (generate these files first)
======================================================================

A1. Folder structure:

design-patterns-handbook/
├── mkdocs.yml
├── requirements.txt
├── README.md
├── .github/workflows/deploy.yml          # build + deploy to GitHub Pages
├── docs/
│   ├── index.md                          # landing page with hero + cards grid
│   ├── getting-started/
│   │   ├── how-to-use.md                 # learning paths per audience
│   │   ├── oop-solid.md                  # OOP, SOLID, DRY, KISS, YAGNI, composition>inheritance
│   │   └── uml-cheatsheet.md             # class/sequence diagrams in Mermaid
│   ├── creational/   (singleton, factory-method, abstract-factory, builder, prototype, object-pool)
│   ├── structural/   (adapter, bridge, composite, decorator, facade, flyweight, proxy)
│   ├── behavioral/   (chain-of-responsibility, command, interpreter, iterator, mediator,
│   │                  memento, observer, state, strategy, template-method, visitor,
│   │                  null-object, specification)
│   ├── microservices/
│   │   ├── decomposition.md              # by business capability / subdomain (DDD)
│   │   ├── api-gateway.md
│   │   ├── backend-for-frontend.md
│   │   ├── service-discovery.md
│   │   ├── circuit-breaker.md
│   │   ├── retry-timeout-bulkhead.md
│   │   ├── rate-limiter.md
│   │   ├── saga.md                       # choreography + orchestration
│   │   ├── outbox.md                     # transactional outbox + CDC
│   │   ├── cqrs.md
│   │   ├── event-sourcing.md
│   │   ├── database-per-service.md
│   │   ├── strangler-fig.md
│   │   ├── sidecar-service-mesh.md
│   │   ├── idempotent-consumer.md
│   │   ├── distributed-tracing.md        # observability: logs, metrics, traces
│   │   ├── config-server.md
│   │   └── anti-corruption-layer.md
│   ├── concurrency/  (producer-consumer, thread-pool, future-promise, read-write-lock,
│   │                  immutable-object, double-checked-locking, virtual-threads)
│   ├── architecture/ (layered, hexagonal, clean, event-driven, modular-monolith)
│   ├── anti-patterns.md
│   ├── pattern-selection-guide.md        # decision tree: "which pattern when?"
│   ├── comparisons.md                    # Strategy vs State, Decorator vs Proxy,
│   │                                     # Adapter vs Facade, Factory vs Builder, etc.
│   ├── interview/
│   │   ├── index.md                      # how pattern rounds work at each company type
│   │   ├── google.md
│   │   ├── microsoft.md
│   │   ├── amazon.md
│   │   ├── sap.md
│   │   ├── service-companies.md          # Cognizant, TCS, Infosys, Accenture, Wipro
│   │   ├── lld-problems.md               # Parking Lot, Elevator, BookMyShow, Splitwise,
│   │   │                                 # Rate Limiter, LRU Cache, Logger, Vending Machine,
│   │   │                                 # Food Delivery, Grocery Ordering — each solved
│   │   │                                 # with patterns, class diagram and Java code
│   │   ├── system-design-patterns.md     # HLD round: which microservice pattern and why
│   │   ├── top-100-questions.md          # graded Beginner / Mid / Senior / Staff
│   │   └── revision-cheatsheet.md        # one-page summary of every pattern
│   ├── projects/
│   │   └── grocery-platform.md           # capstone: one end-to-end grocery delivery
│   │                                     # system that uses 15+ patterns together
│   └── glossary.md
└── code/                                 # Maven multi-module project, one module per
    ├── pom.xml                           # category, so every example in the docs runs
    ├── creational/ structural/ behavioral/ microservices/ concurrency/

A2. mkdocs.yml must use Material for MkDocs with:
- theme.name: material, custom_dir: overrides (optional)
- palette: light + dark toggle (scheme default/slate), primary indigo, accent deep purple,
  following the user's OS preference
- font: text "Inter", code "JetBrains Mono"
- features: navigation.instant, navigation.instant.progress, navigation.tracking,
  navigation.tabs, navigation.tabs.sticky, navigation.sections, navigation.expand,
  navigation.path, navigation.indexes, navigation.top, navigation.footer, toc.follow,
  search.suggest, search.highlight, search.share, content.code.copy,
  content.code.annotate, content.code.select, content.tabs.link, content.tooltips,
  announce.dismiss
- markdown_extensions: admonition, pymdownx.details, pymdownx.superfences (with a
  mermaid custom fence), pymdownx.tabbed (alternate_style: true), pymdownx.highlight
  (anchor_linenums, line_spans, pygments_lang_class), pymdownx.inlinehilite,
  pymdownx.snippets, pymdownx.emoji (material icons), pymdownx.tasklist,
  pymdownx.mark, pymdownx.keys, attr_list, md_in_html, def_list, abbr, footnotes,
  tables, toc (permalink: true)
- plugins: search, tags, minify (optional), git-revision-date-localized (optional),
  glightbox (optional)
- extra: social links, version notice, tags page
- extra_css: stylesheets/extra.css (hero section, card grids, difficulty badges
  🟢 Beginner / 🟡 Intermediate / 🔴 Advanced, company badges)
- Full `nav:` that matches the folder structure above

A3. requirements.txt with pinned versions of mkdocs-material and the plugins.
A4. GitHub Actions workflow that runs `mkdocs gh-deploy --force` on push to main.
A5. Landing page (docs/index.md) with a hero banner, a "Choose your path" card grid
    (Beginner / Experienced / Interview Sprint), a Mermaid map of pattern categories,
    and a "How each page is structured" section.

======================================================================
SECTION B — MANDATORY TEMPLATE FOR EVERY PATTERN PAGE
======================================================================
Every pattern page (GoF, concurrency, architecture AND microservice) must follow this
exact structure, with these headings, in this order:

---
tags: [creational | structural | behavioral | microservices | concurrency, <difficulty>]
---
# <Pattern Name>

!!! abstract "TL;DR"
    One sentence definition + one sentence on when to use it.

Badges: difficulty, how often it's asked in interviews (⭐ 1–5), category.

## 1. Intent & Problem It Solves
   - Plain-English problem statement, then the formal GoF/microservices.io definition.

## 2. Real-World Analogy (Beginner-friendly)
   - A non-software analogy (restaurant, airport, bank, grocery store).

## 3. Structure
   - Mermaid class diagram (for GoF) or Mermaid architecture/sequence diagram
     (for microservices).
   - Table of participants and their responsibilities.

## 4. Naive Approach (Without the Pattern)
   - Short code showing the problem: if/else chains, tight coupling, etc. Explain why
     it breaks: which SOLID principle it violates.

## 5. Three Real-Time Examples (MANDATORY — exactly 3, each fully worked)
   Each example must come from a DIFFERENT industry domain. Choose from: e-commerce,
   grocery delivery, banking/payments, ride-hailing, food delivery, healthcare,
   streaming (Netflix/Spotify-like), logistics, SaaS/ERP (SAP-like), social media,
   telecom, travel booking.

   For EACH of the 3 examples use this sub-structure:

   ### Example N: <Title> (<Domain>)
   **Scenario:** 4–6 sentences describing the real business context, the scale (e.g.
   "50k orders/min at peak") and the constraints.
   **Design:** Mermaid diagram specific to this example.
   **Full Code:** Complete, runnable Java 21 code split into files using
   `=== "FileName.java"` content tabs. Include a `Main`/`Demo` class (or for
   microservices, a Spring Boot app + controller + config + a curl command / test).
   Use code annotations (# (1)!) to explain the important lines.
   **Output:** The EXACT console output (or HTTP request/response, or log lines) of
   running the demo, in a ```text block. The output must match what the code prints,
   character for character. For microservices, also show the failure-path output (for
   example circuit OPEN, saga compensation, retry exhausted).
   **Why this pattern here:** 3–5 bullets linking the scenario to the pattern's benefits.
   **Unit Test:** A JUnit 5 test for this example (in a collapsible `??? example` block).

## 6. Variations & Modern Java Take
   - e.g. enum Singleton, Builder with records, Strategy with lambdas/functional
     interfaces, sealed-interface State machines, virtual-thread-friendly versions.

## 7. Where It Lives in Real Frameworks
   - Concrete references: JDK classes (java.util, java.io, java.time), Spring
     (BeanFactory, JdbcTemplate, ApplicationEvent, AOP proxies), Hibernate, Kafka,
     Netflix OSS, Resilience4j, Kubernetes.

## 8. Pros, Cons & Trade-offs
   - Table. Include performance, memory, testability, readability.

## 9. Pitfalls & Anti-Patterns (Experienced Section)
   !!! danger blocks: thread-safety, reflection/serialization attacks, over-engineering,
   distributed-systems failure modes (for microservices: dual writes, cascading
   failures, retry storms, poison messages, clock skew).

## 10. Senior / Architect Deep Dive (10–15 yrs)
   - Concurrency and memory-model concerns, scaling, observability, testing strategy,
     migration from legacy code, how to defend this choice in a design review, and when
     NOT to use it. Include one "war story": a realistic production incident and the
     fix.

## 11. Related Patterns & Comparisons
   - Comparison table against the 2–3 patterns it gets confused with most.

## 12. Interview Corner
   Split into tabs:
   === "🟢 Beginner / Service Companies"
       5 Q&A typical of Cognizant/TCS/Infosys/Accenture/Wipro rounds
       (definitions, "write a thread-safe Singleton", differences).
   === "🟡 Mid-Level"
       5 Q&A (code changes, extensibility, testing).
   === "🔴 Senior / FAANG / SAP"
       5 Q&A in the style of Google/Microsoft/Amazon/SAP: open-ended design, scaling,
       trade-offs, follow-up chains ("now make it distributed", "now handle 10x load").
   === "💻 Coding Challenge"
       1 hands-on problem with a hidden solution in a `??? success "Solution"` block.
   Every answer must be a model answer the candidate could say out loud, with the key
   phrase interviewers listen for in **bold**.

## 13. Quick Revision Card
   !!! tip "Remember in 30 seconds"
   5 bullets + a one-line code skeleton.

======================================================================
SECTION C — EXTRA RULES FOR MICROSERVICE PATTERN PAGES
======================================================================
- Each of the 3 examples must be a small but realistic Spring Boot 3 service (or a
  pair of services) with: `pom.xml` dependencies snippet, `application.yml`, domain
  model (records), controller, service, the pattern implementation (e.g. Resilience4j
  config, Kafka listener, outbox poller, saga orchestrator) and a demo run.
- Show BOTH the happy path and the failure path in the Output section, with realistic
  log lines (timestamps, traceId, service name).
- Include a docker-compose.yml snippet when infrastructure is needed (Kafka, Postgres,
  Redis, Eureka/Consul, Zipkin/Jaeger).
- Include a sequence diagram for the request flow and, for Saga/Outbox/CQRS/Event
  Sourcing, a state diagram.
- Add a "Production Checklist" (idempotency keys, timeouts, observability, alerting,
  back-pressure, data consistency guarantees).
- Add "How to explain this in a System Design interview in 2 minutes".

Suggested domains for microservice examples (pick 3 different ones per page):
  Circuit Breaker → Grocery checkout calling Payment, Ride-hailing calling Maps ETA,
                    Streaming app calling Recommendation service
  Saga            → Grocery order (Order→Inventory→Payment→Delivery), Travel booking
                    (Flight+Hotel+Car), Bank fund transfer across ledgers
  API Gateway     → Mobile grocery app, Banking open-API, Multi-tenant SaaS (SAP-like)
  CQRS            → Product catalog search, Trading order book, Hospital patient records
  Outbox          → Order-created event, Payment-captured event, User-registered event
  (Use similarly distinct triads for every other microservice pattern.)

======================================================================
SECTION D — INTERVIEW SECTION REQUIREMENTS
======================================================================
- interview/index.md: a table of how design pattern / LLD / HLD rounds differ at
  Google, Microsoft, Amazon, SAP, Atlassian, Uber vs. Cognizant, TCS, Infosys,
  Accenture, Wipro, Capgemini (round names, what's evaluated, typical time, level).
- One page per company type with: round format, 15 commonly reported questions with
  model answers, rubric ("what a Strong Hire answer includes"), and a 4-week
  preparation plan.
- lld-problems.md: at least 10 classic LLD problems, each with requirements →
  entities → class diagram (Mermaid) → patterns used and why → full Java code →
  sample output → follow-up questions.
- top-100-questions.md: grouped by level, each with a concise model answer.
- revision-cheatsheet.md: one table row per pattern (intent, key classes, JDK/Spring
  example, one-liner to say in an interview).
- Add a "Behavioural tie-in" box: how to describe using a pattern in a STAR-format
  story, for experienced candidates.
- Don't invent claims about specific companies' confidential question banks. Phrase
  them as "commonly reported" or "typical style".

======================================================================
SECTION E — CAPSTONE PROJECT (docs/projects/grocery-platform.md)
======================================================================
Design a grocery delivery platform (like Instacart/BigBasket/Blinkit) end to end:
- Services: User, Catalog, Cart, Order, Inventory, Payment, Delivery, Notification,
  Pricing/Promotions, API Gateway.
- Map at least 15 patterns to concrete places in the system (for example Builder for
  Order, Strategy for pricing/discount, State for order lifecycle, Observer/events for
  notifications, Chain of Responsibility for checkout validation, Saga for order
  placement, Outbox for events, CQRS for catalog search, Circuit Breaker for payment,
  Rate Limiter at the gateway).
- Provide a C4-style Mermaid architecture diagram, sequence diagrams for "place order"
  and "payment failure → compensation", key Java code and sample output.

======================================================================
SECTION F — WRITING & QUALITY RULES
======================================================================
1. Tone: friendly mentor for beginners, then precise and opinionated in Senior
   sections. Short paragraphs. No filler.
2. Use Material features everywhere: admonitions (note, tip, warning, danger,
   example, question, success), content tabs, code annotations, Mermaid, tables,
   tooltips/abbreviations, task lists for checklists, keyboard keys.
3. Every page ends with "Next →" / "← Previous" links and a "Practice" task list.
4. Code style: meaningful names, final fields, immutability by default, records for
   DTOs, no System.exit, no unchecked casts, logging via System.out for GoF demos and
   SLF4J for Spring demos.
5. Outputs must be deterministic. Avoid random values and timestamps unless they're
   fixed or clearly marked as sample.
6. Self-check before finishing each page: (a) exactly 3 examples, (b) each has
   Scenario, Design, Full Code, Output, Why, Unit Test, (c) code compiles in your head
   with imports, (d) output matches the code, (e) the Interview Corner has all 4 tabs.
   Fix any gaps before moving on.

======================================================================
EXECUTION PLAN (follow strictly)
======================================================================
Step 1: Output Section A in full (mkdocs.yml, requirements.txt, README, workflow,
        index.md, extra.css, the getting-started pages, and the root Maven pom.xml).
Step 2: Generate ONE complete reference page (creational/singleton.md) following the
        template, then STOP and ask: "Reference page done — continue with the rest of
        Creational?"
Step 3: On "continue", generate the pages category by category: Creational →
        Structural → Behavioral → Concurrency → Microservices → Architecture →
        Comparisons/Selection Guide/Anti-patterns → Interview → Capstone → Glossary.
        Produce at most 2 pattern pages per response so nothing gets truncated. At the
        end of each response, print a progress checklist of completed and remaining
        pages.
Step 4: Finally, output the commands to run locally:
          python -m venv .venv && source .venv/bin/activate
          pip install -r requirements.txt
          mkdocs serve        # http://127.0.0.1:8000
          mkdocs build --strict
          cd code && mvn -q test
        and the steps to publish on GitHub Pages.

Output each file as:
### `path/to/file.ext`
```<lang>
...full file content...
```
Never write "..." or "rest of code omitted" inside a file.

Start now with Step 1.
```

---

## How to use this prompt

1. Paste the whole block into a new chat with a capable model (Claude Opus or Sonnet is recommended because of the long context).
2. After each batch, reply `continue`. The model keeps a progress checklist, so you can resume in a new chat by pasting the prompt plus the checklist and saying "continue from here".
3. Save the files into a repository, then run `pip install -r requirements.txt && mkdocs serve`.
4. Run `mkdocs build --strict` and `mvn test` in `code/` to catch broken links and code that doesn't compile.

## Optional add-ons (append to the prompt if you want them)

- **Hindi/regional explanations:** "Add a short `??? info "Explain in Hindi"` block after the analogy in every page."
- **Kotlin parity:** "Add a Kotlin tab next to every Java code tab."
- **Quiz mode:** "At the end of each page, add 5 MCQs with answers hidden in a `??? question` block."
- **PDF export:** "Add the `mkdocs-with-pdf` plugin configuration so the handbook can be exported as a single PDF."
