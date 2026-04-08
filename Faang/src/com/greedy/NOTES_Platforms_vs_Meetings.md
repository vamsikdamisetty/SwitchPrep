# MinimumPlatforms vs NMeetings — Comparison Notes

## At a Glance

| Aspect               | NMeetings (Activity Selection)         | MinimumPlatforms                              |
|-----------------------|----------------------------------------|-----------------------------------------------|
| **Question asked**    | Max non-overlapping meetings we can pick | Min platforms to serve ALL trains              |
| **Can we skip events?** | ✅ Yes — we choose a subset            | ❌ No — every train must be accommodated       |
| **Strategy**          | Greedy selection (pick earliest ending) | Count maximum concurrent overlap               |
| **Data structure**    | Single `endTime` variable              | Two pointers on independently sorted arrays    |
| **Time Complexity**   | O(n log n)                             | O(n log n)                                     |
| **Space Complexity**  | O(n) (Meeting objects)                 | O(1)                                           |

---

## Why NMeetings uses a single `endTime` and it works

```java
// NMeetings: We CHOOSE which meetings to take
if (meeting.getStart() > endTime) {
    meetingCount++;
    endTime = meeting.getEnd();   // only ONE meeting active at a time
}
```

We are **selecting** non-overlapping meetings. At any point only **one meeting is active**,
so tracking a single `endTime` is sufficient.

---

## Why MinimumPlatforms CANNOT use a single `endTime`

```java
// MinimumPlatforms: We must handle ALL trains simultaneously
while (j < dep.length && dep[j] < arr[i]) {
    j++;
    platsUsed--;
}
platsUsed++;
```

All trains must be served. Multiple trains can be at the station at once,
so we must track **all** active departures — a single `endTime` loses information.

---

## ❌ Why the "single endTime" approach FAILS for Platforms

### Proposed (incorrect) approach:
> Put arrival and departure into one object, sort by departure,
> loop through, track one `endTime`. If arrival <= endTime → increase platforms,
> else reset.

### Counterexample:

```
Train A: arr=900,  dep=1200
Train B: arr=950,  dep=1000
Train C: arr=1100, dep=1300
```

Sorted by departure: B(950–1000), A(900–1200), C(1100–1300)

- B: platforms=1, endTime=1000
- A: arr=900 ≤ 1000 → platforms=2, endTime=1200
- C: arr=1100 ≤ 1200 → platforms=3  ← **WRONG!**

**Correct answer is 2.** At time 1100, train B has already left (dep=1000).
Only A and C overlap. The single `endTime` approach lost track of B's departure
when it was overwritten with 1200.

---

## Overlap Condition Difference (< vs <=)

| Problem          | Condition to free a resource | Why                                              |
|------------------|------------------------------|--------------------------------------------------|
| **MinPlatforms** | `dep[j] < arr[i]`           | Train departing at 1100 & arriving at 1100 **share the platform** (simultaneous presence) |
| **MinMeetingRooms** | `dep[j] <= arr[i]`       | Meeting ending at 1100 & starting at 1100 **can reuse the room** (one ends, next begins)   |

### Example:
```
Event 1: [900, 1100]
Event 2: [1100, 1300]
```
- **Platforms**: dep=1100, arr=1100 → overlap → **2 platforms**
- **Meeting Rooms**: end=1100, start=1100 → no conflict → **1 room**

---

## Summary

- **NMeetings** = "How many can I pick?" → Greedy selection, single `endTime` works.
- **MinPlatforms** = "How many resources to serve all?" → Count all concurrent events, need two-pointer approach.
- Applying the NMeetings pattern (single `endTime`) to MinPlatforms **will produce wrong results**.

