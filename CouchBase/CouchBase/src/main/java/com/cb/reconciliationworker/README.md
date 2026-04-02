# Reconciliation Worker

A self-healing background service that automatically detects and fixes
**zombie / orphan jobs** in a distributed system — jobs that are stuck in
`RUNNING` state because the worker process that owned them crashed before it
could write a terminal status.

---

## The Problem

```
Worker A           Database
   │                  │
   ├── claim job ───► │  status = RUNNING
   │                  │
   ✗ (crash)          │  status = RUNNING  ← stuck forever!
```

In any distributed job queue, a worker can die at any moment (OOM kill,
network partition, hardware failure).  If it never writes `COMPLETED` or
`FAILED` back to the database, the job stays `RUNNING` indefinitely.
Downstream systems (dashboards, retry logic, SLAs) will be misled.

---

## The Solution — Reconciliation Loop

A dedicated background thread periodically scans the job store and cross-
checks every long-running job against the live state of worker processes.
If no live worker owns a job → it is a zombie → mark it `FAILED`.

```
Every 10 seconds:
  ┌─────────────────────────────────────────────┐
  │  1. Find RUNNING jobs older than 5 s        │
  │  2. For each → ask WorkerStateService       │
  │        "Is anyone actually running this?"   │
  │  3. If NO → fix() → status = FAILED         │
  └─────────────────────────────────────────────┘
```

---

## Architecture

```
Scheduler
    │
    │  scheduleAtFixedRate (every 10 s)
    ▼
ReconciliationWorker (Runnable)
    ├── JobRepository.findRunningJobsOlderThan(5000 ms)
    │       └── ConcurrentHashMap (in-memory store)
    └── WorkerStateService.isActuallyRunning(jobId)
            └── (real: service registry / heartbeat check)
```

---

## Class Reference

### `JobStatus` _(enum)_
| Value | Meaning |
|-------|---------|
| `RUNNING` | Job has been claimed and is (supposed to be) executing |
| `COMPLETED` | Job finished successfully |
| `FAILED` | Job crashed, timed-out, or was reconciled as a zombie |

---

### `Job`
Data model representing one unit of work.

| Field | Type | Description |
|-------|------|-------------|
| `jobId` | `String` | Unique identifier |
| `status` | `JobStatus` | Current lifecycle state |
| `lastUpdatedTime` | `long` | Epoch-ms of last status write; drives staleness detection |

---

### `JobRepository`
Thread-safe in-memory store backed by `ConcurrentHashMap`.

| Method | Description |
|--------|-------------|
| `save(Job)` | Insert or overwrite a job |
| `findRunningJobsOlderThan(ms)` | Return RUNNING jobs not updated within `ms` milliseconds |
| `updateStatus(jobId, status)` | Idempotently set a new status and refresh the timestamp |

> **Production note:** Replace the `ConcurrentHashMap` with CouchBase / Redis /
> PostgreSQL calls while keeping the same method signatures.

---

### `WorkerStateService`
Abstracts the "ground truth" check — is a live process actually running this job?

| Method | Description |
|--------|-------------|
| `isActuallyRunning(jobId)` | Returns `true` if a live worker owns the job, `false` otherwise |

> **Production note:** Implement this by querying a service registry (ZooKeeper,
> Consul, Kubernetes), checking a heartbeat key in Redis/CouchBase, or calling a
> health-check endpoint on the worker pod.

> **Demo behaviour:** `Math.random() > 0.5` — ~50 % of stale jobs are reported
> as still running (legitimately slow), the other ~50 % as dead (zombie).

---

### `ReconciliationWorker`
Core reconciliation logic; implements `Runnable`.

```
run()
 └─► findRunningJobsOlderThan(TIMEOUT=5000)
       for each stale job:
         if NOT isActuallyRunning(jobId)
           fix(job) → updateStatus(FAILED)
                    → print "Reconciled job: <id>"
```

Key properties:
- **Idempotent** — calling `fix()` on an already-`FAILED` job is harmless.
- **Non-blocking** — uses the caller's thread; no internal thread creation.
- **Conservative** — a job confirmed as still running is left untouched and
  re-evaluated on the next tick.

---

### `Scheduler` _(main entry point)_
Wires everything together and drives the reconciliation loop.

```java
Executors.newScheduledThreadPool(1)
    .scheduleAtFixedRate(worker,
        /*initialDelay=*/ 0,
        /*period=*/       10,
        TimeUnit.SECONDS);
```

| Parameter | Value | Rationale |
|-----------|-------|-----------|
| `initialDelay` | 0 s | Start immediately |
| `period` | 10 s | Reconcile at most once every 10 s |
| Pool size | 1 thread | Sequential sweeps; no concurrent overlap |

---

## Sequence Diagram

```
Scheduler          ReconciliationWorker    JobRepository     WorkerStateService
    │                      │                    │                   │
    │ scheduleAtFixedRate   │                    │                   │
    │──────────────────────►│                    │                   │
    │                       │ findRunningJobs    │                   │
    │                       │───────────────────►│                   │
    │                       │◄───────────────────│ [job1, job2]      │
    │                       │                    │                   │
    │                       │        isActuallyRunning(job1)         │
    │                       │───────────────────────────────────────►│
    │                       │◄─────────────────────────────────────── false
    │                       │                    │                   │
    │                       │ updateStatus(job1, FAILED)             │
    │                       │───────────────────►│                   │
    │                       │                    │                   │
    │                       │        isActuallyRunning(job2)         │
    │                       │───────────────────────────────────────►│
    │                       │◄─────────────────────────────────────── true  (skip)
    │                       │                    │                   │
```

---

## Design Principles Demonstrated

| Principle | How it appears here |
|-----------|---------------------|
| **Reconciliation / Self-healing** | Background loop corrects divergence between desired state (`RUNNING`) and actual state (no live process) |
| **Idempotency** | `fix()` / `updateStatus()` can be called any number of times safely |
| **Separation of Concerns** | `JobRepository` owns persistence; `WorkerStateService` owns live-state truth; `ReconciliationWorker` owns the reconciliation logic |
| **Thread Safety** | `ConcurrentHashMap` prevents data races between the main thread and the reconciler thread |
| **Open/Closed Principle** | Swap in a real DB or real service registry without touching `ReconciliationWorker` |

---

## How to Run

```bash
# From the project root
mvn compile
mvn exec:java -Dexec.mainClass="com.cb.reconciliationworker.Scheduler"
```

Expected output (approximately, due to randomness):
```
Reconciled job: job1
Reconciled job: job2
```

After the first reconciliation sweep (~5–10 s) both zombie jobs will have been
detected.  Because their status is now `FAILED`, subsequent sweeps will no
longer find them in `findRunningJobsOlderThan` and the output will go quiet.

---

## Extending to Production

1. **Persistent store** — Replace `JobRepository`'s `ConcurrentHashMap` with
   CouchBase N1QL queries or a Spring Data repository.
2. **Real live-state check** — Implement `WorkerStateService.isActuallyRunning`
   using ZooKeeper ephemeral nodes, a Redis heartbeat TTL, or Kubernetes pod
   status API.
3. **Retry on FAILED** — After `fix()`, publish a message to a Kafka / SQS
   queue so a healthy worker can pick up and re-process the job.
4. **Metrics & Alerting** — Increment a Prometheus counter inside `fix()` to
   track the rate of zombie jobs detected over time.
5. **Distributed reconcilers** — If multiple reconciler instances run in parallel,
   add a distributed lock (e.g. CouchBase `getAndLock`, Redis `SET NX`) around
   `fix()` to ensure only one instance reconciles any given job.

