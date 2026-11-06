# Concurrency Patterns

Concurrency patterns solve **coordination between threads**: sharing work, sharing data safely, and waiting for results, without races, deadlocks or wasted CPU. Every example in this section runs on **Java 21** and uses virtual threads where they make sense.

| Pattern | Problem it solves | Interview frequency | Status |
|---|---|---|---|
| [Producer-Consumer](producer-consumer.md) | Decouple work producers from workers with a buffer and back-pressure | ⭐⭐⭐⭐⭐ | ✅ |
| [Thread Pool](thread-pool.md) | Reuse a bounded set of threads for many tasks | ⭐⭐⭐⭐⭐ | ✅ |
| [Future / Promise](future-promise.md) | Represent a result that will be available later; compose async steps | ⭐⭐⭐⭐ | ✅ |
| [Read-Write Lock](read-write-lock.md) | Many concurrent readers, exclusive writers | ⭐⭐⭐ | ✅ |
| [Immutable Object](immutable-object.md) | Share data between threads with no locks at all | ⭐⭐⭐⭐ | ✅ |
| [Double-Checked Locking](double-checked-locking.md) | Lazy, thread-safe initialization with minimal locking | ⭐⭐⭐⭐ | ✅ |
| [Virtual Threads](virtual-threads.md) | Millions of cheap threads for blocking I/O (Java 21) | ⭐⭐⭐⭐⭐ | ✅ |
| [Guarded Suspension & Balking](guarded-suspension-balking.md) | Wait until a condition holds, or skip work that's already in progress | ⭐⭐⭐⭐ | ✅ |
| [Active Object](active-object.md) | Give an object its own thread and queue; callers get futures, no locks | ⭐⭐⭐ | ✅ |
| [Fork/Join](fork-join.md) | Split CPU-bound work recursively across cores with work stealing | ⭐⭐⭐⭐ | ✅ |

!!! tip "The three questions behind every concurrency bug"
    **Visibility**: will other threads *see* my write? **Atomicity**: can another thread see a half-done update? **Ordering**: can the JVM or CPU reorder my operations? The Java Memory Model's **happens-before** rules answer all three. Every pattern here is a way to get those guarantees right.

## See every pattern here in motion

Pick any pattern below to watch it play, step through it, light up its code, and switch to **Without it** to see what goes wrong without it.

<iframe class="motion-embed motion-family" src="../motion/index.html?family=concurrency&amp;embed=1" title="Animated player for every concurrency pattern" loading="lazy"></iframe>
