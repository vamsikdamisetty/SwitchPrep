# Rate Limiter – Token Bucket

A per-user rate limiter implemented in Java using the **Token Bucket** algorithm. Each user gets their own independent bucket, allowing short bursts while enforcing a sustained request rate.

---

## How It Works

```
Client
  └─ RateLimiter.allow(userId)
        └─ ConcurrentHashMap<userId, TokenBucket>
                └─ TokenBucket.allowRequest()
                      ├─ refill()   ← top up tokens based on elapsed time
                      └─ consume 1 token if available → ALLOWED / REJECTED
```

1. `Client` calls `RateLimiter.allow(userId)` for each incoming request.
2. `RateLimiter` lazily creates one `TokenBucket` per user (stored in a `ConcurrentHashMap`).
3. `TokenBucket.allowRequest()` refills the bucket based on elapsed time, then either consumes a token (`true`) or rejects the request (`false`).

---

## Token Bucket Algorithm

Each bucket has two settings:

| Setting      | Value | Meaning                                      |
|--------------|-------|----------------------------------------------|
| `capacity`   | 4     | Max tokens held at once — controls burst size |
| `refillRate` | 5/sec | Tokens added per second — sustained rate      |

**Refill formula:**
```
tokensToAdd = elapsedSeconds × refillRate
tokens      = min(capacity, tokens + tokensToAdd)
```

**On every request:**
- Refill first, then check.
- If `tokens ≥ 1` → consume 1 token → **ALLOWED**
- If `tokens < 1` → **REJECTED**

---

## Project Structure

```
ratelimiter/
├── TokenBucket.java    # Token bucket logic (refill + allow/reject)
├── RateLimiter.java    # Per-user bucket registry
├── Client.java         # Validation scenarios
└── README.md
```

---

## Classes

### `TokenBucket`
Implements the token bucket algorithm for a single user.

| Member            | Description                                               |
|-------------------|-----------------------------------------------------------|
| `capacity`        | Max tokens the bucket can hold                            |
| `refillRate`      | Tokens added per second                                   |
| `tokens`          | Current token count (fractional between refills)          |
| `lastRefillTime`  | Timestamp of last refill (ms) used to calculate the delta |
| `allowRequest()`  | Refills then returns `true` (allowed) or `false` (rejected) |
| `refill()`        | Adds tokens proportional to elapsed time, capped at capacity |

> `allowRequest()` is `synchronized` — safe for concurrent access from multiple threads.

### `RateLimiter`
Manages one `TokenBucket` per user using a `ConcurrentHashMap`.

| Method              | Description                                              |
|---------------------|----------------------------------------------------------|
| `allow(userId)`     | Returns `true` if the user's bucket has a token available |

Buckets are created lazily on first request via `computeIfAbsent`, so no pre-registration of users is needed.

### `Client`
Validation entry point that exercises the rate limiter across four scenarios.

---

## Validation Scenarios

### Scenario 1 – Burst Limit
Fires `capacity + 2` (6) rapid requests for `userA`.

- Requests 1–4 → `ALLOWED` (bucket starts full)
- Requests 5–6 → `REJECTED` (bucket exhausted)

### Scenario 2 – Rejection While Empty
Immediately fires 3 more requests on the already-empty bucket.

- All 3 → `REJECTED`

### Scenario 3 – Refill Recovery
Waits `capacity / refillRate` seconds (≈1 s) for the bucket to refill, then fires 4 requests.

- All 4 → `ALLOWED`

### Scenario 4 – Per-User Isolation
Exhausts `userB`'s bucket, then sends requests for `userC`.

- `userB` requests 1–4 → `ALLOWED`, request 5 → `REJECTED`
- `userC` requests 1–3 → `ALLOWED` (completely unaffected by `userB`)

---

## Running

**Prerequisites:** Java 21, Maven

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.cb.ratelimiter.Client"
```

### Sample Output

```
============================================================
  Scenario 1 – Burst limit (expect 4 allowed, 2 rejected)
============================================================
  Request 1 → ALLOWED
  Request 2 → ALLOWED
  Request 3 → ALLOWED
  Request 4 → ALLOWED
  Request 5 → REJECTED
  Request 6 → REJECTED
  Result: 4 allowed, 2 rejected  [expected: 4 allowed, 2 rejected]

============================================================
  Scenario 2 – Rejection while bucket is empty (all should be REJECTED)
============================================================
  Request 1 → REJECTED
  Request 2 → REJECTED
  Request 3 → REJECTED

============================================================
  Scenario 3 – Refill recovery (waiting 1000 ms for bucket to refill)
============================================================
  Request 1 → ALLOWED
  Request 2 → ALLOWED
  Request 3 → ALLOWED
  Request 4 → ALLOWED

============================================================
  Scenario 4 – Per-user isolation (userB exhausted, userC should be unaffected)
============================================================
  Exhausting userB...
  userB request 1 → ALLOWED
  userB request 2 → ALLOWED
  userB request 3 → ALLOWED
  userB request 4 → ALLOWED
  userB request 5 → REJECTED
  Sending fresh requests for userC...
  userC request 1 → ALLOWED
  userC request 2 → ALLOWED
  userC request 3 → ALLOWED
```

---

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| `ConcurrentHashMap` | Lock-free per-user bucket lookup under concurrent load |
| `computeIfAbsent` | Atomically creates a bucket only on first request — no pre-registration needed |
| `synchronized` on `allowRequest()` | Prevents race conditions when multiple threads hit the same user's bucket simultaneously |
| Lazy refill (on demand) | No background thread required — tokens are calculated from elapsed time on each call |
| Fractional `tokens` (`double`) | Accurately accumulates partial tokens between fast successive calls |

