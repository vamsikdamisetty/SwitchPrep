# 🔒 Parking Lot — Concurrency Interview Points

---

## 1. Singleton Thread-Safety

**Q:** You've used `synchronized` on `getInstance()`. What's the performance cost? Can you do better?

**A:** `synchronized` on every call is expensive — it forces all threads to serialize even after the instance is created. Better alternatives:
- **Double-checked locking** — only synchronizes on first creation; requires `volatile` on the instance field to prevent instruction reordering.
- **Bill Pugh (static inner class)** — leverages classloader guarantees; the inner class is loaded lazily and the JVM ensures thread-safe initialization. Zero synchronization overhead.

---

## 2. Race Condition on Spot Allocation (TOCTOU)

**Q:** Two threads find the same spot via `findSpot()`. How do you prevent both from parking?

**A:** The original design had a **TOCTOU (Time-of-Check-Time-of-Use)** vulnerability — the gap between `findSpot()` returning a spot and `parkVehicle()` locking it allowed two threads to claim the same spot.

**Fix:** Use `AtomicBoolean` with `compareAndSet(false, true)` (CAS) inside `ParkingSpot.parkVehicle()`. Only one thread wins the CAS; the loser gets `false` and retries. This is **lock-free** and avoids coarse-grained synchronization.

```java
public boolean parkVehicle(Vehicle vehicle) {
    if (occupied.compareAndSet(false, true)) {
        this.parkedVehicle = vehicle;
        return true;   // won the race
    }
    return false;      // another thread got it
}
```

---

## 3. ConcurrentHashMap vs synchronized

**Q:** Why not rely on `ConcurrentHashMap` alone?

**A:** `ConcurrentHashMap` is thread-safe for **single operations** (`put`, `get`, `remove`) but **not compound operations** (check-then-act across multiple calls).

| Operation | Safe with CHM alone? |
|---|---|
| `activeTickets.put(k, v)` | ✅ Single atomic op |
| `activeTickets.remove(k)` | ✅ Atomic get-and-remove |
| `get()` → then `remove()` | ❌ Compound — needs atomic `remove()` return |
| Update `parkedVehicle` + `occupied` together | ❌ Two fields, not a map — needs `synchronized` or CAS |

**Bottom line:** CHM protects the *map itself*. `synchronized`/CAS protects *business logic* spanning multiple fields or steps.

---

## 4. Deadlock Prevention — Lock Ordering

**Q:** `parkVehicle()` locks Floor → Spot, `unparkVehicle()` locks Spot → Floor. Deadlock?

**A:** Yes — **circular wait** causes deadlock. Prevention: enforce a **strict global lock ordering**:

| Level | Resource | Lock Order |
|---|---|---|
| 1 | `ParkingLot` | Outermost |
| 2 | `ParkingFloor` (by floor ID) | Middle |
| 3 | `ParkingSpot` (by spot ID) | Innermost |

**Rules:**
1. Always acquire locks **top-down** (Lot → Floor → Spot), never reverse.
2. When locking **peers** at the same level, lock by **ascending ID** to prevent peer deadlocks.

A strict ordering eliminates circular wait — one of the four Coffman conditions for deadlock.

---

## 5. Atomicity of Park/Unpark (Compensating Rollback)

**Q:** Park flow spans: find spot → mark occupied → create ticket → store in map. How to make it atomic?

**A:** Since there's no cross-object transaction in Java, use **compensation**:

- **Exception mid-flow →** `try-catch` + rollback (`spot.unparkVehicle()`) to release the CAS-claimed spot.
- **JVM crash / power loss →** **Startup reconciliation** — scan all spots where `occupied == true` but no matching ticket exists, and free them.
- **Database-backed system →** Wrap all steps in an **ACID transaction**.

These are the same patterns used in **distributed sagas**.

---

## 6. Read-Write Contention on `displayAvailability()`

**Q:** Iterating spots while others park/unpark — inconsistent reads?

**A:** Yes — **dirty read / torn snapshot**. A thread can see a state that never existed at any single point in time.

**Solutions by use case:**

| Scenario | Solution |
|---|---|
| Display is rare (admin dashboard) | `ReadWriteLock` — readers share, writers are exclusive |
| Display is frequent (real-time board) | **Snapshot copy** — `new ArrayList<>(spots.values())` then iterate lock-free |
| Writes are very frequent | Keep per-spot CAS, accept eventual consistency on reads |

**ReadWriteLock trade-off:** Great read-read concurrency, but **writers block on all readers finishing** — can cause writer starvation if readers are frequent. Use `fair = true` mode to mitigate.

---

## 7. Strategy Object Thread-Safety

**Q:** `setFeeStrategy()` called while another thread is mid-fee-calculation?

**A:** Without `volatile`, the JVM is **not required** to make the write visible to other threads (CPU caching). The reading thread may use a stale strategy indefinitely.

| Concern | `volatile` sufficient? |
|---|---|
| Visibility of strategy swap | ✅ Yes |
| Single reference assignment | ✅ Yes (atomic by JLS) |
| Swapping two strategies as a pair | ❌ No |
| Mutable/stateful strategy objects | ❌ No |

**For atomic pair swaps:** Wrap both strategies in an **immutable config object** and swap the single `volatile` reference — the **volatile publication idiom**.

```java
private volatile ParkingConfig config;  // immutable, holds both strategies

public void updateConfig(ParkingStrategy ps, FeeStrategy fs) {
    this.config = new ParkingConfig(ps, fs);  // single atomic reference swap
}
```

---

## 8. Scaling Beyond `synchronized`

**Q:** 10,000 concurrent vehicles across 50 floors — how to scale?

**A:**
- **Fine-grained locking** — per-spot locks (or CAS) instead of per-floor `synchronized`.
- **`ReentrantLock` with `tryLock(timeout)`** — avoids indefinite blocking; thread can give up and try another spot.
- **Lock-free structures** — `AtomicBoolean` for spot occupancy (already implemented).
- **Partitioning** — each floor handled by an independent thread pool; eliminates cross-floor contention entirely.

The key principle: **reduce lock scope and contention surface** as throughput requirements grow.

---

## 9. The ABA Problem

**Q:** Between check and act, a spot is parked → unparked → appears available again. State changed twice but looks the same. Safe?

**A:** In our design, **ABA is not a problem** because:
- CAS on `AtomicBoolean` only cares about the *current value* (`false` → `true`), not the history.
- If the spot was freed and is genuinely available, claiming it is correct behavior.

**When ABA matters:** If the CAS target is a reference or counter where history matters (e.g., lock-free stacks/queues). Solution: `AtomicStampedReference` which tracks a version stamp alongside the value.

---

## 10. Testing Concurrency

**Q:** How to prove two threads cannot park in the same spot simultaneously?

**A:** Use concurrency test primitives to **maximize contention** and assert correctness:

| Primitive | Role |
|---|---|
| `CyclicBarrier(n)` | All `n` threads block until everyone arrives, then start CAS simultaneously |
| `ExecutorService` | Manages thread pool, returns `Future` for result collection |
| `CountDownLatch(n)` | Main thread waits until all `n` worker threads signal completion |
| `Future.get()` | Blocks until thread result is available for assertion |

**Three tests implemented in `ConcurrencyTest.java`:**

| Test | Proves |
|---|---|
| `twoThreadsCannotParkInSameSpot` | CAS on one `ParkingSpot` — exactly one wins (XOR) |
| `twoThreadsCompeteForLastSpotViaParkingLot` | End-to-end through strategy + retry — only one ticket issued |
| `stressTest_100ThreadsFor10Spots` | 100 threads, 10 spots — exactly 10 succeed, 90 fail, zero double-allocation |

---

## Quick Reference — Concurrency Primitives Used

| Primitive | Where Used | Purpose |
|---|---|---|
| `AtomicBoolean` + CAS | `ParkingSpot.parkVehicle()` | Lock-free spot claiming |
| `ConcurrentHashMap` | `activeTickets`, floor `spots` | Thread-safe single-op map access |
| `volatile` | Strategy fields | Visibility across threads |
| `synchronized` | `getInstance()`, `unparkVehicle()` | Mutual exclusion for compound ops |
| `ReadWriteLock` | `displayAvailability()` (recommended) | Read-read concurrency with write exclusivity |
| Bounded retries (`MAX_RETRIES`) | `ParkingLot.parkVehicle()` | Prevent livelock on CAS failure |

