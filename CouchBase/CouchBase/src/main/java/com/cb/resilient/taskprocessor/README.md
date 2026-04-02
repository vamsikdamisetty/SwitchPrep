# Resilient Task Processor

A multi-threaded Java task processing system that demonstrates three core reliability patterns: **concurrency**, **idempotency**, and **retry with back-off**.

---

## How It Works

```
Main
 └─ ResilientTaskProcessor
       ├─ BlockingQueue        ← tasks wait here
       ├─ ExecutorService      ← fixed thread pool processes them
       ├─ ConcurrentHashMap    ← tracks already-processed task IDs
       └─ TaskProcessor        ← does the actual work (30% random failure)
```

1. `Main` creates a `ResilientTaskProcessor` and starts its dispatcher loop on a background thread.
2. Tasks are submitted to an unbounded `BlockingQueue`.
3. The dispatcher pulls tasks from the queue and hands each one to the thread pool.
4. Each worker thread calls `handleTask()`, which enforces all three reliability patterns.

---

## Reliability Patterns

### 1. Concurrency
A fixed thread pool (`Executors.newFixedThreadPool(n)`) processes multiple tasks in parallel. The dispatcher loop is non-blocking — it offloads each task to the pool immediately and loops back to pick up the next one.

### 2. Idempotency
Before processing, each task's `taskId` is looked up in a `ConcurrentHashMap`. If it is already present (i.e. it was successfully processed before), the task is silently skipped. This prevents duplicate work if the same task is submitted more than once.

```
submitted: task-1  →  processed  →  recorded in map
submitted: task-1  →  "Skipping duplicate: task-1"  (no work done)
```

### 3. Retry with Linear Back-off
If `TaskProcessor.process()` throws an exception, the task is re-queued up to **3 times**. Before each re-queue, the thread sleeps for `attempt × 1 second` to give the system time to recover:

| Attempt | Back-off delay |
|---------|---------------|
| 1       | 1 s           |
| 2       | 2 s           |
| 3       | 3 s           |

After 3 failed attempts the task is logged as permanently failed and discarded.

---

## Project Structure

```
src/main/java/com/cb/resilient/taskprocessor/
├── Main.java                  # Entry point — wires everything together
├── ResilientTaskProcessor.java # Core engine (queue, pool, idempotency, retry)
├── Task.java                  # Data model (taskId, payload, attempt counter)
└── TaskProcessor.java         # Worker — simulates 30% random failures
```

---

## Classes

### `Task`
Holds the data for a single unit of work.

| Field     | Type     | Description                              |
|-----------|----------|------------------------------------------|
| `taskId`  | `String` | Unique identifier (used for idempotency) |
| `payload` | `String` | Input data to be processed               |
| `attempt` | `int`    | Number of processing attempts so far     |

### `TaskProcessor`
Executes the actual work for a task. Throws a `RuntimeException` with 30% probability to simulate transient failures (e.g. network timeouts, I/O errors).

### `ResilientTaskProcessor`
The core engine. Accepts tasks via `submit()`, dispatches them to a thread pool, and handles idempotency + retries internally.

| Method          | Description                                          |
|-----------------|------------------------------------------------------|
| `submit(task)`  | Enqueues a task (non-blocking)                       |
| `start()`       | Runs the dispatcher loop (blocking — run on its own thread) |
| `handleTask()`  | Idempotency check → process → retry on failure       |
| `retry(task)`   | Sleeps for back-off delay, then re-enqueues the task |

### `Main`
Demo entry point. Submits 10 unique tasks (`task-0` … `task-9`) and one intentional duplicate (`task-1`) to exercise all three patterns.

---

## Running

**Prerequisites:** Java 21, Maven

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.cb.resilient.taskprocessor.Main"
```

### Sample Output

```
Processed payload data_1000 for: task-0
Retrying: task-2 attempt=1
Processed payload data_1001 for: task-1
Skipping duplicate: task-1
Processed payload data_1002 for: task-2
Failed permanently: task-5
```

---

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| `LinkedBlockingQueue` | Unbounded; producers never block on `offer()` |
| `ConcurrentHashMap` | Lock-free reads for the idempotency check under concurrent load |
| `synchronized` not needed on `handleTask` | Each task is owned by exactly one worker thread at a time |
| Linear back-off (not exponential) | Keeps retry windows predictable; swap `attempt` for `2^attempt` to make it exponential |

