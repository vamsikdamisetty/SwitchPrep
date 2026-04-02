# LRU Cache — Low-Level Design Explanation

## Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [What is an LRU Cache?](#2-what-is-an-lru-cache)
3. [Requirements Recap](#3-requirements-recap)
4. [Core Data Structure: HashMap + Doubly-Linked List](#4-core-data-structure-hashmap--doubly-linked-list)
5. [Why This Combination?](#5-why-this-combination)
6. [Class Diagram](#6-class-diagram)
7. [Detailed Walkthrough of Operations](#7-detailed-walkthrough-of-operations)
8. [Concurrency & Thread Safety](#8-concurrency--thread-safety)
9. [Why Not Just `synchronized`?](#9-why-not-just-synchronized)
10. [Complexity Analysis](#10-complexity-analysis)
11. [Code Walkthrough](#11-code-walkthrough)
12. [Visual Step-by-Step Example](#12-visual-step-by-step-example)
13. [Test Coverage Summary](#13-test-coverage-summary)
14. [Trade-offs & Alternatives](#14-trade-offs--alternatives)
15. [Possible Extensions](#15-possible-extensions)

---

## 1. Problem Statement

We are building a high-throughput API server. Expensive database queries need to be
cached in memory to reduce latency. The cache must:

- Hold a **fixed number** of entries (bounded memory).
- **Evict the least recently used** entry when full.
- Be accessed by **hundreds of threads** simultaneously.
- Deliver **O(1)** `get` and `put` performance.

---

## 2. What is an LRU Cache?

**LRU (Least Recently Used)** is a cache eviction policy that discards the entry
that has not been accessed for the longest time.

**Intuition:** If you haven't looked at something in a while, you're unlikely to
need it again soon — so it's the safest thing to throw away when space runs out.

```
Access sequence:  A  B  C  D  A  E   (capacity = 3)

After A:          [A]
After B:          [B, A]
After C:          [C, B, A]
After D:          [D, C, B]         ← A evicted (LRU)
After A:          [A, D, C]         ← A accessed, promoted to MRU; B was already gone
After E:          [E, A, D]         ← C evicted (LRU)
```

---

## 3. Requirements Recap

| Requirement            | Detail                                          |
|------------------------|------------------------------------------------|
| **Capacity**           | Fixed at construction time (N items)            |
| **Eviction**           | Least Recently Used item removed when full      |
| **`get(key)`**         | Return value or `null`; mark as recently used   |
| **`put(key, value)`**  | Insert/update; evict LRU if over capacity       |
| **Thread Safety**      | Hundreds of concurrent threads                  |
| **Time Complexity**    | O(1) for both `get` and `put`                   |

---

## 4. Core Data Structure: HashMap + Doubly-Linked List

The classic O(1) LRU Cache uses **two data structures working together**:

```
┌─────────────────────────────────────────────────────────────┐
│                    ConcurrentHashMap                         │
│                                                             │
│   Key "A"  ──→  Node{A, val}                                │
│   Key "B"  ──→  Node{B, val}                                │
│   Key "C"  ──→  Node{C, val}                                │
└─────────────────────────────────────────────────────────────┘
                        │
                        │  (Nodes are also linked together)
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                  Doubly-Linked List                          │
│                                                             │
│   [HEAD] ⇄ [C] ⇄ [B] ⇄ [A] ⇄ [TAIL]                      │
│    sentinel  MRU              LRU  sentinel                 │
└─────────────────────────────────────────────────────────────┘
```

**HashMap** → gives us O(1) lookup by key.  
**Doubly-Linked List** → gives us O(1) insertion, removal, and reordering (move-to-front).

The same `Node` object lives in **both** structures — the map stores a reference to
it, and the node contains `prev`/`next` pointers for the list.

---

## 5. Why This Combination?

| Approach                        | `get`   | `put`   | Evict LRU | Problem                        |
|--------------------------------|---------|---------|-----------|-------------------------------|
| HashMap only                   | O(1)    | O(1)    | O(n)      | No ordering → can't find LRU  |
| LinkedList only                | O(n)    | O(n)    | O(1)      | No fast lookup by key          |
| **HashMap + Doubly-Linked List** | **O(1)** | **O(1)** | **O(1)**  | ✅ Best of both worlds         |
| TreeMap (by timestamp)         | O(log n)| O(log n)| O(log n)  | Log factor overhead            |
| `LinkedHashMap` (access-order) | O(1)    | O(1)    | O(1)      | Not thread-safe by default     |

---

## 6. Class Diagram

```
┌───────────────────────┐
│    <<interface>>       │
│     Cache<K, V>       │
├───────────────────────┤
│ + get(key: K): V      │
│ + put(key: K, val: V) │
│ + size(): int         │
└───────────┬───────────┘
            │ implements
            ▼
┌───────────────────────────────────────────┐
│          LRUCache<K, V>                    │
├───────────────────────────────────────────┤
│ - capacity: int                            │
│ - map: ConcurrentHashMap<K, Node<K,V>>     │
│ - lock: ReentrantLock                      │
│ - head: Node<K,V>        ← sentinel       │
│ - tail: Node<K,V>        ← sentinel       │
├───────────────────────────────────────────┤
│ + get(key: K): V                           │
│ + put(key: K, value: V): void              │
│ + size(): int                              │
│ - addToHead(node): void                    │
│ - removeNode(node): void                   │
│ - moveToHead(node): void                   │
│ - removeTail(): Node<K,V>                  │
└───────────────────────────────────────────┘
            │ has-a
            ▼
┌───────────────────────────────────────────┐
│         Node<K, V>  (inner class)          │
├───────────────────────────────────────────┤
│ + key: K          (final)                  │
│ + value: V                                 │
│ + prev: Node<K,V>                          │
│ + next: Node<K,V>                          │
└───────────────────────────────────────────┘
```

---

## 7. Detailed Walkthrough of Operations

### 7.1 `get(key)`

```
1.  Look up key in ConcurrentHashMap           ← lock-free, O(1)
2.  If not found → return null
3.  Acquire ReentrantLock
4.     Re-check the node is still valid        ← double-check pattern
5.     moveToHead(node)                        ← 4 pointer swaps
6.  Release lock
7.  Return node.value
```

**Why the re-check in step 4?**  
Between step 1 (lock-free map read) and step 3 (acquiring the lock), another
thread might have evicted this exact node. Without the re-check, we'd be moving
a stale, unlinked node back into the list — corrupting it.

### 7.2 `put(key, value)`

```
1.  Acquire ReentrantLock
2.  Look up key in map
3.  If key exists:
       a. Update node.value
       b. moveToHead(node)                     ← promote to MRU
4.  If key is new:
       a. Create new Node(key, value)
       b. map.put(key, node)
       c. addToHead(node)
       d. If map.size() > capacity:
            i.  removeTail() → get evicted node
            ii. map.remove(evicted.key)
5.  Release lock
```

**Why lock the entire `put`?**  
Insert + potential eviction must be atomic. If two threads both see
`size == capacity` and both try to insert, we'd end up with `capacity + 2`
entries without the lock.

### 7.3 Linked List Helpers

```java
addToHead(node):         removeNode(node):         moveToHead(node):
  node.prev = head         node.prev.next            removeNode(node)
  node.next = head.next      = node.next             addToHead(node)
  head.next.prev = node    node.next.prev
  head.next = node           = node.prev

removeTail():
  lru = tail.prev
  if lru == head → return null   // empty
  removeNode(lru)
  return lru
```

**Sentinel nodes** (`head` and `tail` are dummy nodes that are never removed)
eliminate all `null` checks. Every real node always has valid `prev` and `next`.

---

## 8. Concurrency & Thread Safety

### The Problem

Hundreds of threads call `get()` and `put()` simultaneously. Without
synchronization:

- **Lost updates**: Two threads overwrite each other's pointer changes.
- **Corrupted list**: A node's `prev`/`next` become inconsistent → infinite
  loops, lost nodes.
- **Size drift**: `map.size()` exceeds capacity because eviction wasn't atomic.

### Our Strategy: ConcurrentHashMap + ReentrantLock

```
┌──────────────────────────────────────────────────────────┐
│  ConcurrentHashMap                                        │
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐                        │
│  │Seg 0│ │Seg 1│ │Seg 2│ │Seg 3│  ← segmented locks     │
│  └─────┘ └─────┘ └─────┘ └─────┘    (internal to CHM)   │
│                                                          │
│  → Lock-free reads (volatile + CAS internally)           │
│  → Fine-grained writes (only locks the bucket)           │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│  ReentrantLock (single lock for the linked list)          │
│                                                          │
│  → Guards only pointer swaps (moveToHead, addToHead,     │
│    removeNode, removeTail)                               │
│  → Critical section is ~4 pointer assignments            │
│  → Very fast → minimal contention                        │
└──────────────────────────────────────────────────────────┘
```

### Why This Works Well

| Aspect               | Detail                                             |
|----------------------|----------------------------------------------------|
| **Read path (get)**  | Map lookup is lock-free; lock held only for 4 pointer swaps |
| **Write path (put)** | Lock held for map put + pointer swaps + possible eviction |
| **Lock granularity** | Single lock, but critical section is tiny (~nanoseconds) |
| **Fairness**         | `ReentrantLock` can be configured as fair if needed |

---

## 9. Why Not Just `synchronized`?

```java
// ❌ Naive approach — terrible throughput
public synchronized V get(K key) { ... }
public synchronized void put(K key, V value) { ... }
```

| Problem                       | Explanation                                       |
|-------------------------------|---------------------------------------------------|
| **Coarse-grained lock**      | Only one thread at a time for ANY operation        |
| **Blocks all readers**       | Even `get()` calls block each other               |
| **No lock-free reads**       | Map lookup could be concurrent but isn't           |
| **Monitor overhead**         | `synchronized` uses the object's intrinsic monitor; `ReentrantLock` is more flexible (tryLock, fairness, interruptibility) |

### Why not `ReadWriteLock`?

A `ReadWriteLock` helps when reads vastly outnumber writes and reads don't
mutate state. But in an LRU cache, **every `get()` mutates the list** (move
to head), so it's effectively a write operation. A `ReadWriteLock` would give
no benefit here.

### Why not a fully lock-free approach?

Lock-free LRU caches exist (e.g., using CAS on atomic references) but are
significantly more complex to implement correctly. Our approach — a single
`ReentrantLock` guarding only pointer swaps — is the sweet spot of simplicity
and performance for most real-world workloads.

---

## 10. Complexity Analysis

| Operation     | Time Complexity | Space Complexity |
|---------------|----------------|-----------------|
| `get(key)`    | **O(1)**       | —               |
| `put(key,val)`| **O(1)**       | —               |
| `size()`      | **O(1)**       | —               |
| **Overall space** | —          | **O(capacity)** |

### Breakdown for `get(key)`:

| Step                    | Cost |
|-------------------------|------|
| HashMap lookup          | O(1) amortized |
| Acquire lock            | O(1) (uncontended) |
| removeNode (2 pointer swaps) | O(1) |
| addToHead (4 pointer swaps)  | O(1) |
| Release lock            | O(1) |

### Breakdown for `put(key, value)`:

| Step                    | Cost |
|-------------------------|------|
| Acquire lock            | O(1) |
| HashMap lookup/insert   | O(1) amortized |
| addToHead               | O(1) |
| removeTail (if evicting)| O(1) |
| HashMap remove (if evicting) | O(1) amortized |
| Release lock            | O(1) |

---

## 12. Visual Step-by-Step Example

**Capacity = 3**

### Step 1: `put("A", 1)`

```
Map: { A → Node(A,1) }

List:  [HEAD] ⇄ [A] ⇄ [TAIL]
               MRU/LRU
```

### Step 2: `put("B", 2)`

```
Map: { A → Node(A,1), B → Node(B,2) }

List:  [HEAD] ⇄ [B] ⇄ [A] ⇄ [TAIL]
                MRU     LRU
```

### Step 3: `put("C", 3)`

```
Map: { A → Node(A,1), B → Node(B,2), C → Node(C,3) }

List:  [HEAD] ⇄ [C] ⇄ [B] ⇄ [A] ⇄ [TAIL]
                MRU               LRU
         ← Cache is now FULL (3/3) →
```

### Step 4: `get("A")` → returns 1, promotes A

```
Map: { A → Node(A,1), B → Node(B,2), C → Node(C,3) }

Before:  [HEAD] ⇄ [C] ⇄ [B] ⇄ [A] ⇄ [TAIL]

  removeNode(A):  [HEAD] ⇄ [C] ⇄ [B] ⇄ [TAIL]    +  [A] (detached)
  addToHead(A):   [HEAD] ⇄ [A] ⇄ [C] ⇄ [B] ⇄ [TAIL]

After:   [HEAD] ⇄ [A] ⇄ [C] ⇄ [B] ⇄ [TAIL]
                  MRU               LRU
```

### Step 5: `put("D", 4)` → cache full, evict LRU ("B")

```
Before:  [HEAD] ⇄ [A] ⇄ [C] ⇄ [B] ⇄ [TAIL]
                                 ↑ LRU — will be evicted

  1. Create Node(D,4)
  2. addToHead(D):  [HEAD] ⇄ [D] ⇄ [A] ⇄ [C] ⇄ [B] ⇄ [TAIL]
  3. size (4) > capacity (3) → evict!
  4. removeTail():  [HEAD] ⇄ [D] ⇄ [A] ⇄ [C] ⇄ [TAIL]  +  [B] (removed)
  5. map.remove("B")

After:
Map: { A → Node(A,1), C → Node(C,3), D → Node(D,4) }

List:  [HEAD] ⇄ [D] ⇄ [A] ⇄ [C] ⇄ [TAIL]
                MRU               LRU
```

### Step 6: `get("B")` → returns `null` (evicted!)

---

## 11. Code Walkthrough

### `Cache.java` — The Interface

```java
public interface Cache<K, V> {
    V get(K key);            // Retrieve & promote to MRU
    void put(K key, V value); // Insert/update, evict if full
    int size();              // Current entry count
}
```

Clean, generic interface. Decoupled from the LRU implementation — could be
swapped with LFU, FIFO, or any other strategy.

### `LRUCache.java` — The Implementation

**Node (inner class):**
```java
private static class Node<K, V> {
    final K key;       // Needed during eviction to remove from map
    V value;
    Node<K, V> prev;
    Node<K, V> next;
}
```

> **Key insight**: The node stores the `key` so that when we evict from the tail
> of the list, we know which key to remove from the HashMap.

**Constructor:**
```java
public LRUCache(int capacity) {
    this.capacity = capacity;
    this.map = new ConcurrentHashMap<>(capacity);
    this.head = new Node<>(null, null);  // sentinel
    this.tail = new Node<>(null, null);  // sentinel
    head.next = tail;
    tail.prev = head;
}
```

**Sentinel pattern**: `head` and `tail` are never data nodes. They exist solely
so that `addToHead`, `removeNode`, etc. never deal with `null` pointers.

---

## 13. Test Coverage Summary

| Test                                              | What it validates                                     |
|---------------------------------------------------|------------------------------------------------------|
| `get_returnsNullForMissingKey`                    | Cache miss returns `null`                             |
| `put_thenGet_returnsValue`                        | Basic insert and retrieval                            |
| `put_overwriteExistingKey_updatesValue`           | Update in place; size unchanged                       |
| `size_reflectsEntryCount`                         | `size()` tracks insertions                            |
| `eviction_removesLeastRecentlyUsed`               | LRU item evicted when capacity exceeded               |
| `eviction_respectsAccessOrder`                    | `get()` promotes item; non-accessed item evicted      |
| `eviction_overwritePromotesAndDoesNotEvict`       | `put()` on existing key promotes it to MRU            |
| `capacityOfOne_evictsImmediately`                 | Edge case: capacity=1                                 |
| `constructor_rejectsNonPositiveCapacity`          | Validation: capacity ≤ 0 throws exception             |
| `concurrency_noExceptionsAndSizeNeverExceedsCapacity` | 100 threads × 1000 ops; no crashes, size ≤ capacity |

---

## 14. Trade-offs & Alternatives

### Our Approach vs. Alternatives

| Approach                              | Pros                              | Cons                                    |
|---------------------------------------|-----------------------------------|-----------------------------------------|
| **Ours: CHM + DLL + ReentrantLock**   | O(1), simple, correct             | Single lock for list mutations          |
| `Collections.synchronizedMap` + `LinkedHashMap` | Very simple code       | Coarse lock; blocks all threads         |
| Caffeine / Guava Cache               | Battle-tested, feature-rich       | External dependency; may be overkill    |
| Striped locking (multiple segments)   | Higher throughput under contention | Much more complex; harder to get right  |
| Lock-free (CAS-based)                | Maximum throughput                | Extremely complex; hard to verify       |

### When to Use Our Approach

✅ You need a lightweight, dependency-free cache.  
✅ Hundreds of threads with moderate contention.  
✅ Interview or LLD design scenario.  

### When to Use Caffeine/Guava Instead

✅ Production system needing TTL, refresh, stats, async loading.  
✅ You want a battle-tested library with years of optimization.  

---

## 15. Possible Extensions

| Extension             | How to Implement                                                        |
|-----------------------|-------------------------------------------------------------------------|
| **TTL (expiration)**  | Add `createdAt`/`expiresAt` to `Node`; lazy cleanup on access or a scheduled reaper thread. |
| **Max memory size**   | Track byte size of values instead of entry count; evict until under limit. |
| **Eviction listener** | Accept a `BiConsumer<K,V>` callback invoked when an entry is evicted.   |
| **Statistics**        | Atomic counters for hits, misses, evictions; expose via `stats()` method. |
| **LFU variant**       | Replace the doubly-linked list with a frequency-based structure (e.g., `LinkedHashSet` per frequency). |
| **Distributed cache** | Layer this as an L1 in-process cache in front of Redis/Memcached (L2).  |

---

## Summary

```
 ┌─────────────┐     O(1) lookup      ┌─────────────────────────┐
 │  Client      │ ──────────────────→  │   ConcurrentHashMap     │
 │  get / put   │                      │   Key → Node reference  │
 └──────┬───────┘                      └───────────┬─────────────┘
        │                                          │
        │  lock (only for list mutation)           │ same Node objects
        ▼                                          ▼
 ┌────────────────────────────────────────────────────────────┐
 │  Doubly-Linked List (with sentinels)                       │
 │                                                            │
 │  [HEAD] ⇄ [MRU] ⇄ [...] ⇄ [...] ⇄ [LRU] ⇄ [TAIL]       │
 │                                                            │
 │  • addToHead  → O(1)   • removeNode → O(1)                │
 │  • moveToHead → O(1)   • removeTail → O(1)                │
 └────────────────────────────────────────────────────────────┘
```

**The key insight**: By combining a HashMap (fast lookup) with a doubly-linked
list (fast reordering), and protecting the list with a minimal lock, we achieve
**O(1) thread-safe LRU caching** suitable for high-throughput production use.

