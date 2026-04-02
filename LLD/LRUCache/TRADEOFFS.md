# LRU Cache — Concurrency Trade-offs & Alternatives

## The Core Problem

Our current `LRUCache` uses a **single `ReentrantLock`** that guards the
doubly-linked list. Every `get()` and `put()` must acquire this lock to
reorder or mutate the list.

```
Thread-1  ──→  lock.lock()  ──→  moveToHead  ──→  lock.unlock()
Thread-2  ──→  lock.lock()  ──→  ⏳ BLOCKED (waiting for Thread-1)
Thread-3  ──→  lock.lock()  ──→  ⏳ BLOCKED
   ...
Thread-100 ─→  lock.lock()  ──→  ⏳ BLOCKED
```

With hundreds of threads, this single lock becomes a **serial bottleneck**.
Even though each critical section is only ~4 pointer swaps (~nanoseconds),
threads queue up behind each other. Under high throughput the lock becomes
the ceiling for the entire system.

---

## Trade-off Spectrum

```
  Simple / Correct                                     Complex / Fastest
  ◄──────────────────────────────────────────────────────────────────────►

  synchronized    Single           Striped        Deferred         Lock-free
  (whole map)     ReentrantLock    Locks          Promotion        (CAS-based /
                  ▲ (our current)                 (buffer+drain)   Caffeine W-TinyLFU)
                  │
                  └── WE ARE HERE
```

Every step to the right **increases throughput** but **sacrifices simplicity,
strict LRU accuracy, or both**.

---

## Option 1: Current Design — Single `ReentrantLock`

### How It Works

```java
// get()
Node node = map.get(key);           // ← lock-free (ConcurrentHashMap)
lock.lock();
    moveToHead(node);               // ← 4 pointer swaps
lock.unlock();

// put()
lock.lock();
    map.put(key, newNode);
    addToHead(newNode);
    if (overCapacity) removeTail();  // evict
lock.unlock();
```

### Scorecard

| Dimension          | Rating       | Notes                                     |
|--------------------|-------------|-------------------------------------------|
| **Correctness**    | ✅ Perfect   | Single lock = trivially serializable       |
| **LRU accuracy**   | ✅ Perfect   | Global ordering always consistent          |
| **Complexity**     | ✅ Simple    | ~200 lines, easy to reason about           |
| **Read throughput** | ⚠️ Limited  | Every `get` acquires write lock            |
| **Write throughput**| ⚠️ Limited  | All writers serialize on same lock         |
| **Contention**     | ❌ High      | 1 lock for all threads                     |

### When to Use

- Low-to-moderate concurrency (< ~16 threads)
- Correctness matters more than throughput
- Interview / LLD demonstration

---

## Option 2: Striped (Segmented) Locks

### The Idea

Split the cache into **N independent segments**, each with its own lock and
its own doubly-linked list. A key is routed to a segment via `hash(key) % N`.

```
                        ┌──── hash(key) % stripeCount ────┐
                        │                                  │
                        ▼                                  ▼
          ┌─────────────────────┐           ┌─────────────────────┐
          │   Segment 0          │           │   Segment 1          │
          │   Lock₀              │           │   Lock₁              │
          │   HashMap₀           │           │   HashMap₁           │
          │   [H]⇄[C]⇄[A]⇄[T]  │           │   [H]⇄[D]⇄[B]⇄[T]  │
          └─────────────────────┘           └─────────────────────┘
                  ...                              ...
          ┌─────────────────────┐           ┌─────────────────────┐
          │   Segment N-2        │           │   Segment N-1        │
          │   Lock_{N-2}         │           │   Lock_{N-1}         │
          │   HashMap_{N-2}      │           │   HashMap_{N-1}      │
          │   [H]⇄[F]⇄[E]⇄[T]  │           │   [H]⇄[G]⇄[T]      │
          └─────────────────────┘           └─────────────────────┘
```

Threads operating on **different segments never block each other**.

### The Trade-off

```
                        Single Lock              Striped Locks
                        ───────────              ─────────────
  LRU ordering:        Global (perfect)    →    Per-segment (approximate)
  Lock contention:     1 bottleneck        →    N independent locks
  Throughput:          Limited              →    ~N× improvement
  Eviction accuracy:   Evicts true LRU     →    Evicts segment-LRU
  Code complexity:     Simple              →    Moderate
```

**Why is eviction only approximate?**

With striped locks, each segment tracks its own access order independently.
The globally least-recently-used item might be in Segment 3, but if Segment 0
is full, it evicts Segment 0's LRU — not the global LRU.

```
Example (capacity=4, 2 stripes, 2 per stripe):

  Segment 0:  [H] ⇄ [C] ⇄ [A] ⇄ [T]     (A is Seg-0 LRU)
  Segment 1:  [H] ⇄ [D] ⇄ [B] ⇄ [T]     (B is Seg-1 LRU)

  True global LRU might be "B" (accessed longest ago).
  But if we put("E") and E hashes to Segment 0,
  we evict "A" (Seg-0 LRU) — not "B" (global LRU).
```

**In practice this rarely matters** — with good hash distribution, the
approximation is very close to true LRU. Production caches like
`ConcurrentHashMap` itself use the exact same segmentation strategy.

### How Many Stripes?

```
  Too few stripes  →  Still contended (approaches single-lock behavior)
  Too many stripes →  Each segment has tiny capacity → poor cache hit rate
  Sweet spot       →  availableProcessors() × 4  (typically 32–64)
```

### Scorecard

| Dimension          | Rating       | Notes                                     |
|--------------------|-------------|-------------------------------------------|
| **Correctness**    | ✅ Correct   | Each segment is independently correct      |
| **LRU accuracy**   | ⚠️ Approx.  | Per-segment LRU, not global               |
| **Complexity**     | ⚠️ Moderate | Segment routing, capacity distribution     |
| **Read throughput** | ✅ Good     | Contention reduced by N×                   |
| **Write throughput**| ✅ Good     | Contention reduced by N×                   |
| **Contention**     | ✅ Low       | Only threads in same segment compete       |

### When to Use

- Moderate-to-high concurrency (16–500+ threads)
- Can tolerate approximate LRU (almost all real-world caches can)
- Want a meaningful improvement without extreme complexity

---

## Option 3: Deferred Promotion (Buffer + Drain)

### The Idea

The key observation: **most of the lock contention comes from `get()` calling
`moveToHead()`**. Reads vastly outnumber writes in most caches.

Solution: **Don't move the node immediately**. Instead, buffer the access event
and drain it later.

```
  get("A"):
    1. map.get("A")                       ← lock-free
    2. buffer.add("A")                    ← lock-free (CAS-based queue)
    3. return value                       ← NO lock acquired!

  Drain (periodic or on put()):
    lock.lock()
    while (!buffer.isEmpty()):
        key = buffer.poll()
        moveToHead(map.get(key))          ← batch pointer swaps
    lock.unlock()
```

### The Trade-off

```
                        Single Lock              Deferred Promotion
                        ───────────              ──────────────────
  get() locking:        Always locks       →    Lock-free (just buffer append)
  Eviction accuracy:    Perfect            →    Slightly stale ordering
  Drain overhead:       None               →    Periodic batch processing
  get() latency:        Lock wait          →    Near-zero (CAS only)
  put() latency:        Same               →    May increase (drains buffer too)
  Code complexity:       Simple             →    Complex (buffer mgmt, drain policy)
```

**Why is eviction slightly stale?**

Between buffer append and drain, the access-order list doesn't reflect the
latest reads. If eviction happens during this window, we might evict an item
that was just accessed but hasn't been promoted yet.

```
Timeline:
  T1: get("A")     → buffer: [A]       (A not yet promoted in list)
  T2: put("Z")     → cache full, evict LRU
                    → list still shows A as LRU (not yet drained!)
                    → A gets evicted even though it was just accessed ❌
  T3: drain()       → too late, A is gone
```

**Mitigation:** Drain the buffer inside `put()` before checking eviction.
This closes most of the window but doesn't eliminate it entirely under
high concurrency.

### Scorecard

| Dimension          | Rating       | Notes                                     |
|--------------------|-------------|-------------------------------------------|
| **Correctness**    | ✅ Correct   | Values are never corrupted                 |
| **LRU accuracy**   | ⚠️ Stale    | Small window where ordering lags behind    |
| **Complexity**     | ❌ Complex   | Buffer sizing, drain triggers, edge cases  |
| **Read throughput** | ✅✅ Excellent | `get()` is lock-free                      |
| **Write throughput**| ✅ Good     | Lock only for puts + batch drain           |
| **Contention**     | ✅✅ Very Low | Only `put` and drain compete               |

### When to Use

- Read-heavy workloads (90%+ reads)
- Can tolerate small eviction inaccuracy window
- Need maximum `get()` throughput

---

## Option 4: Caffeine / W-TinyLFU (Production-Grade)

### The Idea

[Caffeine](https://github.com/ben-manes/caffeine) is the state-of-the-art
Java caching library. It uses **W-TinyLFU** — a combination of:

1. **Window LRU** (small admission buffer, ~1% of capacity)
2. **Segmented LRU** (main cache, ~99% of capacity)
3. **TinyLFU frequency sketch** (probabilistic frequency counter)
4. **Multiple ring buffers** for lock-free access recording
5. **Async drain thread** for batch promotion/eviction

```
  ┌──────────────────────────────────────────────────────┐
  │                    Caffeine Cache                      │
  │                                                       │
  │   ┌─────────┐     TinyLFU      ┌──────────────────┐  │
  │   │ Window   │ ──→ Admission ──→│ Main (Segmented  │  │
  │   │ (1%)     │     Filter       │  LRU, 99%)       │  │
  │   └─────────┘                   └──────────────────┘  │
  │                                                       │
  │   Ring Buffer 0  ─┐                                   │
  │   Ring Buffer 1  ─┤── async drain ──→ reorder lists   │
  │   Ring Buffer 2  ─┤                                   │
  │   Ring Buffer N  ─┘                                   │
  └──────────────────────────────────────────────────────┘
```

### The Trade-off

```
                        Our LRUCache             Caffeine
                        ────────────             ────────
  Eviction policy:      Pure LRU           →    W-TinyLFU (frequency-aware)
  Hit rate:             Good               →    Near-optimal
  Concurrency:          Lock-based         →    Near lock-free
  Code:                 ~200 lines         →    ~10,000+ lines
  Dependency:           None               →    External library
  Features:             get/put only       →    TTL, refresh, stats, async, ...
```

**W-TinyLFU actually outperforms pure LRU** because it considers *frequency*
not just *recency*. A one-time scan of many keys won't pollute the cache
(LRU weakness), because items must pass the frequency filter to be admitted.

### Scorecard

| Dimension          | Rating         | Notes                                    |
|--------------------|---------------|------------------------------------------|
| **Correctness**    | ✅ Proven      | Battle-tested in production at Google-scale |
| **LRU accuracy**   | ✅✅ Better than LRU | Frequency-aware; higher hit rate     |
| **Complexity**     | ✅ (for user)  | Simple API; complexity is internal        |
| **Read throughput** | ✅✅ Excellent | Lock-free reads via ring buffers          |
| **Write throughput**| ✅✅ Excellent | Amortized async drain                    |
| **Contention**     | ✅✅ Near zero | Per-CPU ring buffers                      |

### When to Use

- Production systems at scale
- Need TTL, statistics, async loading, size-based eviction
- Don't want to maintain custom cache code

### Dependency

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>3.1.8</version>
</dependency>
```

---

## Side-by-Side Summary

```
┌─────────────────┬──────────┬────────────┬──────────┬───────────┬────────────┐
│                  │ Single   │ Striped    │ Deferred │ Caffeine  │            │
│   Dimension      │ Lock     │ Locks      │ Promo    │ W-TinyLFU │ Notes      │
│                  │ (ours)   │            │          │           │            │
├─────────────────┼──────────┼────────────┼──────────┼───────────┼────────────┤
│ LRU Accuracy    │ Perfect  │ ~Approx    │ ~Stale   │ Better    │            │
│                  │          │ (segment)  │ (window) │ than LRU  │            │
├─────────────────┼──────────┼────────────┼──────────┼───────────┼────────────┤
│ get() locking   │ Always   │ Per-segment│ None     │ None      │            │
├─────────────────┼──────────┼────────────┼──────────┼───────────┼────────────┤
│ put() locking   │ Always   │ Per-segment│ Always   │ Amortized │            │
├─────────────────┼──────────┼────────────┼──────────┼───────────┼────────────┤
│ Contention      │ High     │ Low        │ Very Low │ Near Zero │            │
│ (100s threads)  │          │ (~N× less) │          │           │            │
├─────────────────┼──────────┼────────────┼──────────┼───────────┼────────────┤
│ Throughput       │ ★☆☆☆    │ ★★★☆      │ ★★★★    │ ★★★★★    │            │
├─────────────────┼──────────┼────────────┼──────────┼───────────┼────────────┤
│ Code Complexity │ Simple   │ Moderate   │ Complex  │ Simple    │ (as user)  │
│                  │ (~200 L) │ (~350 L)   │ (~500 L) │ (library) │            │
├─────────────────┼──────────┼────────────┼──────────┼───────────┼────────────┤
│ External Deps   │ None     │ None       │ None     │ Caffeine  │            │
├─────────────────┼──────────┼────────────┼──────────┼───────────┼────────────┤
│ Best For        │ LLD /    │ High-conc  │ Read-    │ Production│            │
│                  │ Interview│ servers    │ heavy    │ systems   │            │
└─────────────────┴──────────┴────────────┴──────────┴───────────┴────────────┘
```

---

## Decision Flowchart

```
  Start
    │
    ▼
  How many concurrent threads?
    │
    ├── < 16 threads ──────────────→  ✅ Single Lock (our LRUCache)
    │                                     Simple, correct, fast enough.
    │
    ├── 16–500 threads ────────────→  Is approximate LRU acceptable?
    │                                     │
    │                                     ├── Yes ──→  ✅ Striped Locks
    │                                     │               Best throughput/complexity ratio.
    │                                     │
    │                                     └── No ───→  ✅ Deferred Promotion
    │                                                     Lock-free reads, near-exact LRU.
    │
    └── 500+ threads / Production ─→  Do you need TTL, stats, async loading?
                                          │
                                          ├── Yes ──→  ✅ Caffeine (W-TinyLFU)
                                          │               Don't reinvent the wheel.
                                          │
                                          └── No ───→  ✅ Striped Locks
                                                          Lightweight, no dependencies.
```

---

## Why Our Current Design (`LRUCache.java`) Is Still a Good Starting Point

1. **Correctness is non-negotiable.** A single lock makes reasoning trivial —
   there are no subtle race conditions or ordering anomalies.

2. **The critical section is tiny.** `moveToHead` is 4 pointer swaps. Under
   moderate load, threads rarely wait because the lock is held for nanoseconds.

3. **Profile before optimizing.** If profiling shows lock contention is the
   bottleneck (via `jstack`, `async-profiler`, or JFR), *then* graduate to
   striped locks or Caffeine. Premature optimization of a cache that's "fast
   enough" adds complexity for no user-visible benefit.

4. **Interview readiness.** Interviewers expect you to know the single-lock
   design *first*, then discuss trade-offs when asked "what if we need more
   throughput?" — exactly the progression in this document.

---

## Key Takeaway

> **There is no free lunch.** Every concurrency optimization trades away
> either **LRU accuracy**, **code simplicity**, or **adds an external dependency**.
> Choose the simplest design that meets your throughput requirements.

```
  Accuracy ◄──────────────────────► Throughput
             Single Lock  │  Striped  │  Deferred  │  Caffeine
                Perfect   │  ~Good    │  ~Good     │  Better than LRU
                Low       │  High     │  Very High │  Excellent
```

