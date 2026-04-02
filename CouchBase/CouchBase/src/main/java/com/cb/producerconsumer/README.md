# Producer-Consumer Pattern — Interview Prep

---

## What is the Producer-Consumer Pattern?

A classic **concurrency design pattern** where:
- **Producers** generate data/tasks and place them into a shared buffer.
- **Consumers** pick items from that buffer and process them.
- They run **concurrently and independently** — neither knows about the other directly.

```
Producer-1 ──►
                ┌─────────────────────────┐
Producer-2 ──►  │  Shared BlockingQueue   │  ──► Consumer-1
                │  (bounded, capacity=5)  │  ──► Consumer-2
                └─────────────────────────┘  ──► Consumer-3
```

---

## Why Do We Need It?

Without this pattern, tightly coupling producers and consumers creates serious problems:

| Problem | Description |
|---------|-------------|
| **Speed mismatch** | Producer is fast, consumer is slow → producer overwhelms the consumer |
| **Tight coupling** | Producer directly calls consumer → they can't scale independently |
| **No back-pressure** | Unbounded message growth → OutOfMemoryError |
| **Wasted CPU** | Consumer polling in a loop when there's no work → busy-waiting |
| **Thread-safety** | Shared data accessed from multiple threads → race conditions, data corruption |

---

## Expected Problems (Without This Pattern)

### Problem 1 — Race Condition on Shared Data
If two threads push into a plain `ArrayList` simultaneously:
```
Thread-A reads size=5, prepares to write at index 5
Thread-B reads size=5, prepares to write at index 5
Both overwrite the same slot → data lost, size wrong
```
**Root cause:** Non-atomic read-modify-write on unsynchronized data structure.

### Problem 2 — Busy-Waiting / CPU Spin
```java
// BAD: Consumer burns 100% CPU polling an empty queue
while (queue.isEmpty()) { /* do nothing */ }
Message m = queue.remove();
```
**Root cause:** No signalling mechanism — consumer has no way to sleep until work arrives.

### Problem 3 — Unbounded Queue / OOM
```java
// BAD: Producer dumps 10M messages into an unbounded queue
Queue<Message> q = new LinkedList<>();
// Consumer processes at 1/10th the rate → q grows to millions of entries → OOM
```
**Root cause:** No capacity limit → no back-pressure on the producer.

### Problem 4 — Lost Wakeup / Missed Notify
Manual `wait()` / `notify()` code is error-prone:
```java
// BAD: classic manual approach — easy to miss notify, causing a thread to wait forever
synchronized(lock) {
    while (queue.isEmpty()) lock.wait();  // can miss the notify if timing is wrong
    process(queue.remove());
}
```
**Root cause:** The notify can fire *before* the wait, causing the consumer to sleep forever.

### Problem 5 — Graceful Shutdown
How does a consumer know when to stop? If producers simply stop producing, consumers
block on `take()` forever — the JVM never exits.

---

## Solution in This Code

### ✅ Solution 1 — `BlockingQueue` eliminates race conditions
**File:** `Producer.java`, `Consumer.java`

`LinkedBlockingQueue` uses internal `ReentrantLock` with two separate lock objects
(one for `put`, one for `take`) — all access is automatically thread-safe.

```java
// Producer.java — thread-safe enqueue; blocks if full
queue.put(msg);   // ← internally locked, no manual synchronized needed

// Consumer.java — thread-safe dequeue; blocks if empty
Message msg = queue.take();  // ← internally locked
```

### ✅ Solution 2 — `take()` parks the thread (no busy-waiting)
**File:** `Consumer.java`

`BlockingQueue.take()` uses a `Condition.await()` internally. The OS suspends the
consumer thread with **zero CPU usage** until a producer signals it.

```java
// Consumer.java
while (true) {
    Message msg = queue.take(); // Thread PARKS here if queue is empty.
                                // Wakes up the instant a producer calls put().
    processMessage(msg);
}
```

### ✅ Solution 3 — Bounded Queue provides Back-Pressure
**File:** `ProducerConsumerMain.java`

```java
// ProducerConsumerMain.java
BlockingQueue<Message> sharedQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY); // capacity = 5
```

When the queue is full, `put()` in `Producer.java` **blocks the producer thread**
automatically — no code needed. The producer slows to match the consumer rate.

You can observe this in the logs: `queue-latency` values grow as the queue fills up.

### ✅ Solution 4 — No manual wait/notify
`BlockingQueue` wraps `ReentrantLock` + `Condition` internally. You never call
`wait()`, `notify()`, or `notifyAll()` — the lost-wakeup bug is impossible here.

### ✅ Solution 5 — Poison Pill for graceful shutdown
**File:** `Producer.java`, `ProducerConsumerMain.java`

A **sentinel object** signals consumers to stop:

```java
// Producer.java — declared as static final for identity (==) comparison
public static final Message POISON_PILL = new Message(-1, "POISON_PILL");

// After finishing all real messages, each producer sends ONE pill:
queue.put(POISON_PILL);

// Consumer.java — identity check (fast, no .equals() needed)
if (msg == Producer.POISON_PILL) {
    break;   // exit loop cleanly
}

// ProducerConsumerMain.java — re-send exactly NUM_CONSUMERS pills
// so every consumer gets exactly one and exits
for (int i = 0; i < NUM_CONSUMERS; i++) {
    sharedQueue.put(Producer.POISON_PILL);
}
```

### ✅ Solution 6 — Lock-free ID generation with `AtomicInteger`
**File:** `Producer.java`, `ProducerConsumerMain.java`

Multiple producer threads share a single counter. `AtomicInteger.incrementAndGet()`
uses a hardware **CAS (Compare-And-Swap)** instruction — no lock, no contention.

```java
// ProducerConsumerMain.java
AtomicInteger messageIdCounter = new AtomicInteger(0);

// Producer.java — safe from multiple threads simultaneously
int id = messageIdCounter.incrementAndGet();
```

### ✅ Solution 7 — Immutable `Message` for zero-copy safety
**File:** `Message.java`

All fields are `final`. Once constructed, the object can be shared across any
number of threads without synchronization — immutability is the safest form of thread safety.

```java
public final class Message {
    private final int    id;        // set once, never changed
    private final String payload;
    private final long   createdAt;
}
```

---

## `BlockingQueue` API — Quick Reference

| Method | Behaviour when empty/full | When to use |
|--------|--------------------------|-------------|
| `put(e)` | **Blocks** when FULL | Producer — automatic back-pressure |
| `take()` | **Blocks** when EMPTY | Consumer — zero CPU wait |
| `offer(e, t, unit)` | Returns `false` after timeout | Producer with deadline |
| `poll(t, unit)` | Returns `null` after timeout | Consumer with deadline |
| `drainTo(list)` | Non-blocking bulk remove | Batch processing |

## `BlockingQueue` Implementations

| Class | Ordering | Lock strategy | Notes |
|-------|----------|---------------|-------|
| `LinkedBlockingQueue` | FIFO | **2 locks** (put & take separate) | Best throughput — used here |
| `ArrayBlockingQueue` | FIFO | 1 lock | Supports fair ordering |
| `PriorityBlockingQueue` | Priority | 1 lock | Unbounded — no back-pressure |
| `SynchronousQueue` | N/A (no buffer) | CAS | Direct hand-off, zero capacity |
| `DelayQueue` | Delay-based | 1 lock | Items available only after delay |

---

## Execution Flow (with 2 Producers, 3 Consumers, queue cap=5)

```
t=0ms   Producer-1 puts Msg#1  → queue=[1]        Consumer-1 takes Msg#1
t=0ms   Producer-2 puts Msg#2  → queue=[2]        Consumer-2 takes Msg#2
t=50ms  Producer-1 puts Msg#3  → queue=[3]        Consumer-3 takes Msg#3
...
        Queue fills to 5       → Producer.put() BLOCKS  ← back-pressure!
        Consumer drains one    → put() unblocks, Producer continues
...
        All 20 msgs consumed
        Main sends 3 poison pills → Consumer-1, 2, 3 each exit
        ExecutorService shuts down → JVM exits
```

---

## Configuration (in `ProducerConsumerMain.java`)

| Constant | Default | Effect |
|----------|---------|--------|
| `NUM_PRODUCERS` | 2 | Number of producer threads |
| `NUM_CONSUMERS` | 3 | Number of consumer threads |
| `MESSAGES_PER_PRODUCER` | 10 | Messages each producer generates |
| `QUEUE_CAPACITY` | 5 | Max queue size (triggers back-pressure) |
| `Thread.sleep(50)` in Producer | 50 ms | Production rate |
| `Thread.sleep(120)` in Consumer | 120 ms | Processing rate (slower → fills queue) |

---

## Common Interview Questions

**Q: Why `BlockingQueue` instead of `Queue` + `synchronized`?**
> `BlockingQueue` encapsulates the `ReentrantLock`, `Condition.await()`, and
> `Condition.signal()` that you would otherwise write manually — eliminating the
> lost-wakeup bug and reducing boilerplate significantly.

**Q: What is back-pressure and how is it implemented here?**
> Back-pressure is the mechanism by which a slow consumer slows down a fast producer
> automatically. Here, `LinkedBlockingQueue(capacity)` + `put()` provides it for free:
> when the queue is full, `put()` blocks the producer thread.

**Q: What is a Poison Pill?**
> A sentinel value placed into the queue by producers to signal consumers to stop.
> We use a `static final` object so consumers can use identity comparison (`==`) which
> is O(1) and cannot be accidentally confused with a real message.

**Q: How would you scale this to multiple JVMs / microservices?**
> Replace `BlockingQueue` with a distributed broker:
> **Kafka** (high-throughput, persistent, replay), **RabbitMQ** (flexible routing),
> or **AWS SQS** (managed, at-least-once delivery).

**Q: What is the difference between `offer()` and `put()`?**
> `put()` blocks indefinitely when the queue is full.
> `offer(e, timeout, unit)` waits up to a timeout, then returns `false`.
> Use `offer` when you can't afford to block forever (e.g., you want to log a warning
> and drop the message instead of stalling the thread).

**Q: What happens if a consumer crashes mid-processing?**
> The message is lost — this is **at-most-once** delivery. For **at-least-once**,
> acknowledge only after successful processing (like Kafka's manual offset commit),
> and re-queue on failure.

**Q: What is `SynchronousQueue`?**
> A queue with **zero capacity**. Every `put()` blocks until a consumer calls `take()` —
> a direct hand-off. Used internally by `Executors.newCachedThreadPool()`.

---

## Files in This Package

| File | Role |
|------|------|
| `Message.java` | Immutable data holder — thread-safe, no synchronization needed |
| `Producer.java` | Generates messages; uses `put()` for back-pressure; sends poison pill |
| `Consumer.java` | Processes messages; uses `take()` for zero-CPU wait; stops on pill |
| `ProducerConsumerMain.java` | Coordinator: queue, thread pools, poison pill distribution, shutdown |

