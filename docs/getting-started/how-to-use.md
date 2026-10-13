# How to Use This Handbook

This handbook is written for three readers at once. Pick the path that fits you; you can switch at any time.

!!! tip "Every pattern page has the same 13 sections"
    So once you've read one page, you know where everything is on every other page. Start with [Singleton](../creational/singleton.md); it's the reference page.

## Beginner path

**Who:** students, freshers, engineers with 0–2 years of experience.

1. Read [OOP & SOLID Refresher](oop-solid.md) first. Patterns are just SOLID applied to recurring problems.
2. Skim the [UML Cheat Sheet](uml-cheatsheet.md) so the diagrams make sense.
3. On each pattern page, read **sections 1–5** (intent, analogy, structure, naive approach, the 3 examples) and **section 13** (revision card).
4. Run every example yourself. Change one thing and predict the new output before you run it.
5. Do the 🟢 tab in the **Interview Corner**.

Suggested order: Singleton → Factory Method → Builder → Strategy → Observer → Decorator → Adapter → Facade → Template Method → State.

## Experienced path

**Who:** engineers with 5–15+ years, tech leads, architects.

1. Skip the analogies. Go straight to **section 5** and look at how each example is put together.
2. Spend most of your time on **sections 6–10**: modern Java variants, framework internals, trade-offs, pitfalls, the senior deep dive and the war story.
3. Use **section 11** to sharpen how you explain the differences between similar patterns. This is where design reviews are won.
4. Go through the microservices and architecture sections. That's where 10–15-year interviews spend most of their time.

## Interview path

**Who:** anyone with an interview in the next 2–6 weeks.

| Target company type | Focus on | Typical rounds |
|---|---|---|
| Service companies (Cognizant, TCS, Infosys, Accenture, Wipro, Capgemini) | Definitions, Singleton/Factory/Builder/Strategy/Observer code, SOLID, differences between patterns | Technical round 1–2, managerial, HR |
| Product companies (Google, Microsoft, Amazon, SAP, Atlassian, Uber) | Low-level design (LLD) problems, extensibility, concurrency, trade-offs, microservice patterns in system design | Coding ×2–3, LLD/OOD, system design (senior), behavioral |

**4-week plan:**

- [ ] **Week 1:** Creational + SOLID. Write every Singleton variant from memory.
- [ ] **Week 2:** Behavioral (Strategy, Observer, State, Command, Chain of Responsibility, Template Method).
- [ ] **Week 3:** Structural + 5 LLD problems (Parking Lot, Elevator, Vending Machine, LRU Cache, Rate Limiter).
- [ ] **Week 4:** Microservice patterns + mock interviews using the 🔴 tabs.

## Running the code

```bash
git clone https://github.com/khiruashish/java-design-pattern.git
cd java-design-pattern/code
mvn test
java -cp creational/target/classes com.handbook.creational.singleton.grocery.GroceryDemo
```

You need **JDK 21+** and **Maven 3.9+**.

## Conventions used

| Marker | Meaning |
|---|---|
| <span class="badge badge--beginner">🟢 Beginner</span> | Safe for anyone who knows basic Java |
| <span class="badge badge--intermediate">🟡 Intermediate</span> | Needs collections, generics, some concurrency |
| <span class="badge badge--advanced">🔴 Advanced</span> | Memory model, distributed systems, production concerns |
| <span class="badge badge--interview">⭐⭐⭐⭐⭐</span> | How often the pattern comes up in interviews (1–5) |
| `(1)` in code | Click it to open the annotation for that line |
