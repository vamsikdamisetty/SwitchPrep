# 🛗 Elevator System — Low-Level Design (Interview Prep)

---

## 📋 Table of Contents

1. [Problem Statement](#-problem-statement)
2. [Functional Requirements](#-functional-requirements)
3. [Non-Functional Requirements](#-non-functional-requirements)
4. [Core Entities](#-core-entities)
5. [High-Level Architecture](#-high-level-architecture)
6. [Class Diagram](#-class-diagram)
7. [Design Patterns Used](#-design-patterns-used)
   - [State Pattern](#1-state-pattern)
   - [Strategy Pattern](#2-strategy-pattern)
   - [Observer Pattern](#3-observer-pattern)
   - [Singleton Pattern](#4-singleton-pattern)
   - [Facade Pattern](#5-facade-pattern)
8. [Request Flow — Sequence Diagrams](#-request-flow--sequence-diagrams)
9. [State Machine Diagram](#-state-machine-diagram)
10. [Detailed Class Design](#-detailed-class-design)
    - [Elevator Class — Deep Dive](#elevator-core-entity--thread--deep-dive)
11. [Concurrency Model](#-concurrency-model)
12. [Data Structures — Why TreeSet?](#-data-structures--why-treeset)
13. [Example Walkthrough](#-example-walkthrough)
14. [Extensibility & Future Enhancements](#-extensibility--future-enhancements)
15. [Interview Talking Points](#-interview-talking-points)

---

## 📝 Problem Statement

> Design an Elevator System that manages **multiple elevators** in a building.
> The system should efficiently handle **external requests** (hall button presses on a floor)
> and **internal requests** (cabin button presses inside an elevator), move elevators between
> floors, manage direction, and display real-time status updates.

---

## ✅ Functional Requirements

| # | Requirement | Description |
|---|-------------|-------------|
| FR-1 | **Multiple Elevators** | The building has `N` elevators, each operating independently. |
| FR-2 | **External Requests (Hall Calls)** | A user on any floor can press UP/DOWN to request an elevator. |
| FR-3 | **Internal Requests (Cabin Calls)** | A user inside an elevator can press a destination floor button. |
| FR-4 | **Direction Management** | Each elevator maintains its direction: `UP`, `DOWN`, or `IDLE`. |
| FR-5 | **Efficient Movement (SCAN)** | Elevators service all requests in the current direction before reversing (like the disk SCAN algorithm). |
| FR-6 | **Smart Elevator Selection** | External requests are dispatched to the most suitable (nearest, same-direction) elevator. |
| FR-7 | **Real-Time Display** | A display panel shows each elevator's current floor and direction in real time. |
| FR-8 | **Graceful Shutdown** | The system can be shut down cleanly, stopping all elevators and thread pools. |

---

## ⚙️ Non-Functional Requirements

| # | Requirement | Description |
|---|-------------|-------------|
| NFR-1 | **Thread Safety** | Multiple elevators run concurrently; requests can arrive from any thread. |
| NFR-2 | **Extensibility** | Easy to swap elevator selection algorithms or add new states. |
| NFR-3 | **Loose Coupling** | Components communicate through interfaces, not concrete classes. |
| NFR-4 | **Single Responsibility** | Each class has one well-defined responsibility. |

---

## 🧩 Core Entities

```
┌─────────────────────────────────────────────────────────┐
│                    ELEVATOR SYSTEM                       │
│                                                         │
│  ┌──────────────┐   ┌──────────┐   ┌────────────────┐  │
│  │ ElevatorSystem│──▶│ Elevator │──▶│  ElevatorState │  │
│  │  (Singleton)  │   │(Runnable)│   │  (Interface)   │  │
│  └──────┬───────┘   └────┬─────┘   └───────┬────────┘  │
│         │                │                  │           │
│         ▼                ▼                  ▼           │
│  ┌──────────────┐  ┌──────────┐   ┌─────────────────┐  │
│  │  Selection   │  │ Observer │   │   IdleState     │  │
│  │  Strategy    │  │ Display  │   │   MovingUpState  │  │
│  │ (Interface)  │  │          │   │   MovingDownState│  │
│  └──────────────┘  └──────────┘   └─────────────────┘  │
│                                                         │
│  ┌──────────┐  ┌───────────┐  ┌──────────────┐         │
│  │ Request  │  │ Direction │  │ RequestSource│         │
│  │ (Model)  │  │  (Enum)   │  │   (Enum)     │         │
│  └──────────┘  └───────────┘  └──────────────┘         │
└─────────────────────────────────────────────────────────┘
```

---

## 🏗️ High-Level Architecture

```
                        ┌──────────────────────┐
                        │   ElevatorSystemDemo  │
                        │      (Client)         │
                        └──────────┬───────────┘
                                   │
                    requestElevator() / selectFloor()
                                   │
                                   ▼
                     ┌─────────────────────────┐
                     │    ElevatorSystem        │  ◀── Singleton + Facade
                     │  ┌───────────────────┐   │
                     │  │ Selection Strategy │   │  ◀── Strategy Pattern
                     │  └───────┬───────────┘   │
                     │          │ selectElevator │
                     └──────────┼───────────────┘
                                │
               ┌────────────────┼─────────────────┐
               ▼                ▼                  ▼
        ┌────────────┐   ┌────────────┐    ┌────────────┐
        │ Elevator 1 │   │ Elevator 2 │    │ Elevator N │
        │  (Thread)  │   │  (Thread)  │    │  (Thread)  │
        └─────┬──────┘   └─────┬──────┘    └─────┬──────┘
              │                │                  │
              ▼                ▼                  ▼
        ┌────────────┐   ┌────────────┐    ┌────────────┐
        │   State    │   │   State    │    │   State    │  ◀── State Pattern
        │ (Idle/Up/  │   │ (Idle/Up/  │    │ (Idle/Up/  │
        │   Down)    │   │   Down)    │    │   Down)    │
        └─────┬──────┘   └─────┬──────┘    └─────┬──────┘
              │                │                  │
              ▼                ▼                  ▼
        ┌────────────┐   ┌────────────┐    ┌────────────┐
        │  Display   │   │  Display   │    │  Display   │  ◀── Observer Pattern
        │ (Observer) │   │ (Observer) │    │ (Observer) │
        └────────────┘   └────────────┘    └────────────┘
```

---

## 📐 Class Diagram

```
┌──────────────────────────────────────────────────────────────────────────┐
│                              <<enum>>                                    │
│                             Direction                                    │
│─────────────────────────────────────────────────────────────────────────│
│  UP  |  DOWN  |  IDLE                                                   │
└──────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│                              <<enum>>                                    │
│                           RequestSource                                  │
│─────────────────────────────────────────────────────────────────────────│
│  INTERNAL (cabin button)  |  EXTERNAL (hall button)                     │
└──────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────┐        ┌─────────────────────────────────┐
│          Request             │        │     <<interface>>                │
│──────────────────────────────│        │    ElevatorObserver             │
│ - targetFloor : int          │        │─────────────────────────────────│
│ - direction   : Direction    │        │ + update(Elevator) : void       │
│ - source      : RequestSource│        └──────────────┬──────────────────┘
│──────────────────────────────│                       │ implements
│ + getTargetFloor() : int     │                       ▼
│ + getDirection() : Direction │        ┌─────────────────────────────────┐
│ + getSource() : RequestSource│        │       ElevatorDisplay           │
│ + toString() : String        │        │─────────────────────────────────│
└──────────────────────────────┘        │ + update(Elevator) : void       │
                                        └─────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│                          ElevatorSystem  (Singleton)                     │
│──────────────────────────────────────────────────────────────────────────│
│ - instance          : ElevatorSystem (static)                            │
│ - elevators         : Map<Integer, Elevator>                             │
│ - selectionStrategy : ElevatorSelectionStrategy                          │
│ - executorService   : ExecutorService                                    │
│──────────────────────────────────────────────────────────────────────────│
│ + getInstance(int) : ElevatorSystem            ◀── Singleton             │
│ + start() : void                                                         │
│ + requestElevator(int floor, Direction) : void ◀── Facade (External)     │
│ + selectFloor(int elevatorId, int floor) : void◀── Facade (Internal)     │
│ + shutdown() : void                                                      │
└───────────────────────────────┬──────────────────────────────────────────┘
                                │ has-many
                                ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                       Elevator  (implements Runnable)                     │
│──────────────────────────────────────────────────────────────────────────│
│ - id           : int                                                     │
│ - currentFloor : AtomicInteger                                           │
│ - state        : ElevatorState          ◀── State Pattern                │
│ - isRunning    : volatile boolean                                        │
│ - upRequests   : TreeSet<Integer>       (natural order: ascending)        │
│ - downRequests : TreeSet<Integer>       (reverse order: descending)       │
│ - observers    : List<ElevatorObserver> ◀── Observer Pattern             │
│──────────────────────────────────────────────────────────────────────────│
│ + addObserver(ElevatorObserver) : void                                   │
│ + notifyObservers() : void                                               │
│ + setState(ElevatorState) : void                                         │
│ + move() : void   → delegates to state.move(this)                        │
│ + addRequest(Request) : void  (synchronized)                             │
│ + run() : void   → main loop: move() every 1s                           │
│ + stopElevator() : void                                                  │
└──────────────────────────────────────────────────────────────────────────┘
                                │ has-a
                                ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                     <<interface>> ElevatorState                           │
│──────────────────────────────────────────────────────────────────────────│
│ + move(Elevator) : void                                                  │
│ + addRequest(Elevator, Request) : void                                   │
│ + getDirection() : Direction                                             │
└───────┬──────────────────────┬──────────────────────┬────────────────────┘
        │                      │                      │
        ▼                      ▼                      ▼
 ┌──────────────┐    ┌─────────────────┐    ┌──────────────────┐
 │  IdleState   │    │ MovingUpState   │    │ MovingDownState  │
 │──────────────│    │─────────────────│    │──────────────────│
 │ direction:   │    │ direction: UP   │    │ direction: DOWN  │
 │   IDLE       │    │                 │    │                  │
 │──────────────│    │─────────────────│    │──────────────────│
 │ + move()     │    │ + move()        │    │ + move()         │
 │ + addRequest│    │ + addRequest()  │    │ + addRequest()   │
 └──────────────┘    └─────────────────┘    └──────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│               <<interface>> ElevatorSelectionStrategy                     │
│──────────────────────────────────────────────────────────────────────────│
│ + selectElevator(List<Elevator>, Request) : Optional<Elevator>           │
└───────────────────────────────┬──────────────────────────────────────────┘
                                │ implements
                                ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                     NearestElevatorStrategy                              │
│──────────────────────────────────────────────────────────────────────────│
│ + selectElevator(List<Elevator>, Request) : Optional<Elevator>           │
│ - isSuitable(Elevator, Request) : boolean                                │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 🎨 Design Patterns Used

This system uses **5 design patterns**. Here's a deep-dive into each:

---

### 1. State Pattern

> **Intent:** Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

**Problem it solves:** An elevator behaves differently depending on whether it's idle, moving up, or moving down. Without the State pattern, we'd have large `if-else` or `switch` blocks inside `Elevator.move()` and `Elevator.addRequest()`.

```
                    ┌───────────────────────┐
                    │    <<interface>>       │
                    │    ElevatorState       │
                    │───────────────────────│
                    │ + move(Elevator)       │
                    │ + addRequest(Elevator, │
                    │   Request)             │
                    │ + getDirection()       │
                    └───────────┬───────────┘
                       ┌────────┼────────┐
                       ▼        ▼        ▼
                 ┌──────────┐┌───────┐┌──────────┐
                 │IdleState ││MovingUp││MovingDown│
                 │          ││State   ││State     │
                 └──────────┘└───────┘└──────────┘
```

**How it works in code:**

```java
// Elevator delegates behavior to the current state
public void move() {
    state.move(this);       // ← Behavior depends on which state object is set
}

public synchronized void addRequest(Request request) {
    state.addRequest(this, request);  // ← Different logic per state
}
```

**State Transitions:**

| Current State | Condition | Next State |
|---|---|---|
| `IdleState` | `upRequests` not empty | `MovingUpState` |
| `IdleState` | `downRequests` not empty | `MovingDownState` |
| `MovingUpState` | All `upRequests` served | `IdleState` |
| `MovingDownState` | All `downRequests` served | `IdleState` |

**Why this pattern?**
- ✅ Eliminates complex conditional logic
- ✅ Each state encapsulates its own behavior
- ✅ Easy to add new states (e.g., `MaintenanceState`, `EmergencyState`)
- ✅ Follows **Open/Closed Principle** — extend without modifying existing code

---

### 2. Strategy Pattern

> **Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

**Problem it solves:** When an external request comes in, the system must decide **which elevator** to dispatch. The selection algorithm should be swappable at runtime.

```
  ┌────────────────────────────────┐
  │     <<interface>>              │
  │ ElevatorSelectionStrategy      │
  │────────────────────────────────│
  │ + selectElevator(              │
  │     List<Elevator>, Request)   │
  │   : Optional<Elevator>        │
  └────────────┬───────────────────┘
               │ implements
               ▼
  ┌────────────────────────────────┐
  │  NearestElevatorStrategy       │
  │────────────────────────────────│
  │ + selectElevator(...)          │
  │ - isSuitable(Elevator,Request) │ ◀── Picks the closest suitable elevator
  └────────────────────────────────┘
```

**Selection Algorithm (NearestElevatorStrategy):**

```
For each elevator:
  1. Is it IDLE?                               → Suitable ✅
  2. Is it going in the SAME direction
     AND the request floor is "ahead" of it?   → Suitable ✅
  3. Otherwise                                 → Not Suitable ❌

Among suitable elevators → pick the one with minimum |currentFloor - targetFloor|
```

**Why this pattern?**
- ✅ Easy to add alternative strategies:
  - `LeastLoadedStrategy` — pick elevator with fewest pending requests
  - `ZoneBasedStrategy` — assign floor ranges to specific elevators
  - `RoundRobinStrategy` — distribute requests evenly
- ✅ `ElevatorSystem` is decoupled from the algorithm
- ✅ Can change strategy at runtime (dependency injection)

---

### 3. Observer Pattern

> **Intent:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.

**Problem it solves:** The elevator display panel must update in real-time whenever an elevator changes floor or direction. Hard-coding display updates inside `Elevator` would violate SRP.

```
  ┌────────────────────────┐          ┌──────────────────────────┐
  │      Elevator          │ notifies │    <<interface>>          │
  │  (Subject/Observable)  │─────────▶│   ElevatorObserver       │
  │────────────────────────│  update  │──────────────────────────│
  │ - observers: List<...> │          │ + update(Elevator): void │
  │ + addObserver()        │          └────────────┬─────────────┘
  │ + notifyObservers()    │                       │ implements
  └────────────────────────┘                       ▼
                                      ┌──────────────────────────┐
                                      │    ElevatorDisplay       │
                                      │──────────────────────────│
                                      │ + update(Elevator): void │
                                      │   → prints floor & dir   │
                                      └──────────────────────────┘
```

**Notification triggers in Elevator:**

```java
public void setState(ElevatorState state) {
    this.state = state;
    notifyObservers();     // ◀── Direction changed → notify display
}

public void setCurrentFloor(int floor) {
    this.currentFloor.set(floor);
    notifyObservers();     // ◀── Floor changed → notify display
}
```

**Why this pattern?**
- ✅ Elevator doesn't know about concrete display implementations
- ✅ Easy to add more observers: `LoggingObserver`, `AlertObserver`, `MobileAppObserver`
- ✅ Follows **Open/Closed Principle**
- ✅ Decouples data from presentation

---

### 4. Singleton Pattern

> **Intent:** Ensure a class has only one instance, and provide a global point of access to it.

**Problem it solves:** There should be exactly one `ElevatorSystem` managing all elevators in the building.

```java
public class ElevatorSystem {
    private static ElevatorSystem instance;  // ◀── Single instance

    private ElevatorSystem(int numElevators) { ... }  // ◀── Private constructor

    public static synchronized ElevatorSystem getInstance(int numElevators) {
        if (instance == null) {
            instance = new ElevatorSystem(numElevators);
        }
        return instance;
    }
}
```

**Why this pattern?**
- ✅ Prevents duplicate elevator systems
- ✅ `synchronized` keyword ensures thread-safe lazy initialization
- ✅ Single entry point for the entire system

> **Interview Note:** For production code, consider using the **enum singleton** or **Bill Pugh** approach for better thread safety and serialization handling.

---

### 5. Facade Pattern

> **Intent:** Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use.

**Problem it solves:** The client shouldn't need to know about `Elevator`, `Request`, `Strategy`, or `State`. The `ElevatorSystem` class hides all this complexity behind two simple methods.

```
   Client (Demo)
       │
       │  requestElevator(floor, direction)   ◀── Simple API
       │  selectFloor(elevatorId, floor)       ◀── Simple API
       ▼
  ┌─────────────────────────────────────┐
  │         ElevatorSystem (Facade)     │
  │─────────────────────────────────────│
  │  Internally:                        │
  │  1. Creates Request object          │
  │  2. Uses Strategy to pick elevator  │
  │  3. Delegates to Elevator           │
  │  4. Elevator uses State to process  │
  │  5. Observer updates display        │
  └─────────────────────────────────────┘
```

**Why this pattern?**
- ✅ Client interacts with ONE class, not the entire subsystem
- ✅ Hides complexity of request creation, elevator selection, and state management
- ✅ Makes the API intuitive and easy to use

---

## 🔄 Request Flow — Sequence Diagrams

### External Request (Hall Call) Flow

```
  User          ElevatorSystem       Strategy         Elevator         State          Observer
   │                │                   │                │               │               │
   │ requestElevator│                   │                │               │               │
   │ (floor=5,UP)   │                   │                │               │               │
   │───────────────▶│                   │                │               │               │
   │                │  Create Request   │                │               │               │
   │                │  (5, UP, EXTERNAL)│                │               │               │
   │                │                   │                │               │               │
   │                │  selectElevator() │                │               │               │
   │                │──────────────────▶│                │               │               │
   │                │                   │ Find nearest   │               │               │
   │                │                   │ suitable       │               │               │
   │                │   Elevator 1      │ elevator       │               │               │
   │                │◀──────────────────│                │               │               │
   │                │                   │                │               │               │
   │                │  addRequest(req)  │                │               │               │
   │                │──────────────────────────────────▶│               │               │
   │                │                   │                │ addRequest()  │               │
   │                │                   │                │──────────────▶│               │
   │                │                   │                │  (state adds  │               │
   │                │                   │                │   to TreeSet) │               │
   │                │                   │                │               │               │
   │                │                   │  [run loop]    │               │               │
   │                │                   │                │  move()       │               │
   │                │                   │                │──────────────▶│               │
   │                │                   │                │  currentFloor │               │
   │                │                   │                │  changes      │               │
   │                │                   │                │               │  update()     │
   │                │                   │                │──────────────────────────────▶│
   │                │                   │                │               │  [DISPLAY]    │
   │                │                   │                │               │  Floor: 2 UP  │
```

### Internal Request (Cabin Call) Flow

```
  User          ElevatorSystem         Elevator          State
   │                │                     │                │
   │ selectFloor    │                     │                │
   │ (elevId=1,     │                     │                │
   │  floor=10)     │                     │                │
   │───────────────▶│                     │                │
   │                │  Create Request     │                │
   │                │  (10, IDLE,INTERNAL)│                │
   │                │                     │                │
   │                │  addRequest(req)    │                │
   │                │────────────────────▶│                │
   │                │                     │  addRequest()  │
   │                │                     │───────────────▶│
   │                │                     │  (state routes │
   │                │                     │   to up/down   │
   │                │                     │   TreeSet)     │
```

---

## 🔁 State Machine Diagram

```
                         ┌──────────────────────┐
                         │                      │
                         │      IDLE STATE      │
                    ┌───▶│                      │◀───┐
                    │    │  • Checks upRequests  │    │
                    │    │  • Checks downRequests│    │
                    │    └───────┬──────┬────────┘    │
                    │            │      │             │
                    │   upReqs   │      │  downReqs   │
                    │   not empty│      │  not empty  │
                    │            ▼      ▼             │
     upReqs empty   │  ┌─────────┐  ┌──────────┐     │  downReqs empty
     ───────────────┘  │ MOVING  │  │ MOVING   │     └──────────────
                       │   UP    │  │  DOWN    │
                       │  STATE  │  │  STATE   │
                       │─────────│  │──────────│
                       │• floor++│  │• floor-- │
                       │• stop at│  │• stop at │
                       │  targets│  │  targets │
                       │• poll   │  │• poll    │
                       │  TreeSet│  │  TreeSet │
                       └─────────┘  └──────────┘

  ┌──────────────────────────────────────────────────────────────────┐
  │                   DETAILED STATE TRANSITIONS                     │
  ├────────────────┬──────────────────────┬─────────────────────────┤
  │ Current State  │ Trigger              │ Next State              │
  ├────────────────┼──────────────────────┼─────────────────────────┤
  │ Idle           │ upRequests not empty  │ MovingUp               │
  │ Idle           │ downRequests not empty│ MovingDown             │
  │ Idle           │ both queues empty     │ Idle (no change)       │
  │ MovingUp       │ upRequests all served │ Idle                   │
  │ MovingDown     │ downRequests all done │ Idle                   │
  └────────────────┴──────────────────────┴─────────────────────────┘
```

---

## 📦 Detailed Class Design

### Enums

| Enum | Values | Purpose |
|------|--------|---------|
| `Direction` | `UP`, `DOWN`, `IDLE` | Represents the current movement direction of an elevator |
| `RequestSource` | `INTERNAL`, `EXTERNAL` | Distinguishes between cabin button (internal) and hall button (external) requests |

### Request (Model)

| Field | Type | Description |
|-------|------|-------------|
| `targetFloor` | `int` | The floor to go to |
| `direction` | `Direction` | Desired direction (meaningful for external requests) |
| `source` | `RequestSource` | Where the request originated (INTERNAL / EXTERNAL) |

### Elevator (Core Entity + Thread) — Deep Dive

The `Elevator` class is the **heart of the system**. It is the single most important class to
understand because it ties together **three design patterns** (State, Observer, Runnable/Thread)
and handles **concurrency** — all in ~89 lines.

#### 🧬 What is the Elevator class?

It represents a **single physical elevator car** in a building. Each elevator:
- Runs on its **own thread** (implements `Runnable`)
- Maintains its **own request queues** (up & down)
- Delegates behavior to a **State object** (State Pattern)
- Notifies **Observers** on every change (Observer Pattern)

#### 📐 Class Anatomy

```
┌──────────────────────────────────────────────────────────────────┐
│              Elevator  implements Runnable                        │
├──────────────────────────────────────────────────────────────────┤
│                          FIELDS                                  │
│                                                                  │
│  IDENTITY & POSITION                                             │
│  ─────────────────                                               │
│  - id           : final int           Unique elevator number     │
│  - currentFloor : AtomicInteger       Thread-safe floor counter  │
│                                                                  │
│  STATE & LIFECYCLE                                               │
│  ────────────────                                                │
│  - state        : ElevatorState       Current behavior (State    │
│  │                                    Pattern — Idle/Up/Down)    │
│  - isRunning    : volatile boolean    Thread shutdown flag        │
│                                                                  │
│  REQUEST QUEUES (SCAN Algorithm)                                 │
│  ───────────────                                                 │
│  - upRequests   : TreeSet<Integer>    Ascending order  {3,5,7}   │
│  - downRequests : TreeSet<Integer>    Descending order {8,5,3}   │
│                                                                  │
│  OBSERVERS                                                       │
│  ─────────                                                       │
│  - observers    : List<ElevatorObserver>   Display panels, logs  │
├──────────────────────────────────────────────────────────────────┤
│                         METHODS                                  │
│                                                                  │
│  OBSERVER METHODS                                                │
│  + addObserver(ElevatorObserver)   Register + send initial state │
│  + notifyObservers()              Push updates to all observers  │
│                                                                  │
│  STATE PATTERN METHODS                                           │
│  + setState(ElevatorState)        Transition to new state        │
│  + move()                         Delegate to state.move(this)   │
│                                                                  │
│  REQUEST HANDLING                                                │
│  + addRequest(Request)  [synchronized]  Thread-safe enqueue      │
│                                                                  │
│  THREAD LIFECYCLE                                                │
│  + run()                The main loop: move() → sleep(1s)        │
│  + stopElevator()       Sets isRunning = false                   │
│                                                                  │
│  GETTERS / SETTERS                                               │
│  + getId(), getCurrentFloor(), getDirection()                    │
│  + setCurrentFloor(int)  → also calls notifyObservers()         │
│  + getUpRequests(), getDownRequests(), isRunning()               │
└──────────────────────────────────────────────────────────────────┘
```

#### 🏗️ Constructor — What Happens at Creation

```java
public Elevator(int id) {
    this.id = id;
    this.currentFloor = new AtomicInteger(1);         // Start at ground floor
    this.upRequests = new TreeSet<>();                 // Natural ascending: {3, 5, 7}
    this.downRequests = new TreeSet<>((a, b) -> b - a); // Reverse descending: {8, 5, 3}
    this.state = new IdleState();                      // Starts idle
}
```

| What | Why |
|------|-----|
| `AtomicInteger(1)` | Thread-safe; the `run()` loop and `addRequest()` may access from different threads |
| `TreeSet<>()` for UP | Natural order ensures `.first()` always returns the **nearest floor above** |
| `TreeSet<>((a,b)->b-a)` for DOWN | Reverse comparator ensures `.first()` returns the **nearest floor below** |
| `new IdleState()` | Every elevator starts idle — no movement until a request arrives |

#### 🔄 The `run()` Loop — Elevator's Heartbeat

```java
@Override
public void run() {
    while (isRunning) {        // ① Check shutdown flag
        move();                // ② Delegate to current state
        try {
            Thread.sleep(1000); // ③ Simulate 1 second per floor
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            isRunning = false;  // ④ Graceful shutdown on interrupt
        }
    }
}
```

```
  Thread starts
       │
       ▼
  ┌──────────┐     ┌──────────┐     ┌──────────┐
  │isRunning? │─NO─▶│  STOP    │     │          │
  │           │     │  Thread  │     │          │
  └─────┬─────┘     └──────────┘     │          │
    YES │                             │          │
        ▼                             │          │
  ┌──────────┐                        │          │
  │  move()  │  ← delegates to       │          │
  │          │    state.move(this)    │          │
  └─────┬────┘                        │          │
        │                             │  LOOP    │
        ▼                             │  every   │
  ┌──────────┐                        │  1 sec   │
  │ sleep    │                        │          │
  │ (1000ms) │                        │          │
  └─────┬────┘                        │          │
        │                             │          │
        └─────────────────────────────┘          │
```

**Key Insight:** The elevator doesn't decide *how* to move — it calls `state.move(this)` and
the **current state object** handles everything. This is the **State Pattern** in action.

#### 🎯 `addRequest()` — Thread-Safe Request Handling

```java
public synchronized void addRequest(Request request) {
    System.out.println("Elevator " + id + " processing: " + request);
    state.addRequest(this, request);
}
```

**Why `synchronized`?** Multiple threads can call `addRequest()` simultaneously:
- The `ElevatorSystem` dispatches external requests from the main thread
- Internal requests may come from different threads
- The `run()` loop reads from the same TreeSets

Without `synchronized`, two threads could corrupt the TreeSet concurrently.

**What happens inside `state.addRequest()`?**

Each state routes the request to the correct TreeSet:

```
                          addRequest(floor=7)
                                │
                    ┌───────────┼───────────┐
                    ▼           ▼           ▼
              ┌──────────┐┌──────────┐┌──────────┐
              │IdleState ││MovingUp  ││MovingDown│
              │          ││State     ││State     │
              ├──────────┤├──────────┤├──────────┤
              │if floor > ││if going  ││if going  │
              │current:  ││same dir  ││same dir  │
              │ → upReqs ││& ahead:  ││& ahead:  │
              │if floor < ││ → upReqs ││ → downReq│
              │current:  ││else:     ││else:     │
              │ → downReq││ → downReq││ → upReqs │
              └──────────┘└──────────┘└──────────┘
```

#### 👁️ Observer Notification Points

The Elevator notifies observers at **exactly two critical moments**:

```
  ┌────────────────────────────────────────────┐
  │         WHEN OBSERVERS ARE NOTIFIED        │
  ├────────────────────────────────────────────┤
  │                                            │
  │  1. setState(newState)                     │
  │     → Direction changed (IDLE→UP, UP→IDLE) │
  │     → Display updates: "Direction: UP"     │
  │                                            │
  │  2. setCurrentFloor(floor)                 │
  │     → Elevator moved to a new floor        │
  │     → Display updates: "Floor: 5"          │
  │                                            │
  │  3. addObserver(observer)                  │
  │     → Initial state push on registration   │
  │     → Display shows: "Floor: 1, IDLE"      │
  └────────────────────────────────────────────┘
```

This means every single floor change and every direction change triggers a
real-time display update — without the Elevator knowing anything about the display.

#### 🧵 Concurrency Details in Elevator

| Field | Mechanism | Why |
|-------|-----------|-----|
| `currentFloor` | `AtomicInteger` | Lock-free read/write from multiple threads. The `run()` thread writes (via state), other threads read (via `getCurrentFloor()` in strategy) |
| `isRunning` | `volatile` | Ensures the `run()` loop sees the updated value immediately when `stopElevator()` is called from another thread |
| `addRequest()` | `synchronized` | Prevents race conditions when modifying TreeSets. The `run()` loop polls from TreeSets while external threads add to them |

```
  Main Thread                    Elevator Thread (run loop)
  ───────────                    ─────────────────────────
       │                                │
       │ addRequest(floor=5)            │ move()
       │ [synchronized lock]            │ [reads upRequests.first()]
       │    │                           │    │
       │    ▼                           │    ▼
       │ upRequests.add(5)              │ currentFloor++ 
       │ [release lock]                 │ upRequests.pollFirst()
       │                                │
```

> **Interview Note:** `AtomicInteger` is sufficient here because floor updates are
> simple set operations. If we needed compound operations (check-then-act), we'd
> need explicit locks or `synchronized` blocks.

#### 🔗 How Elevator Connects to All Patterns

```
                    ┌─────────────────────────────────────┐
                    │            Elevator                   │
                    │                                       │
                    │  ┌─────────────────────────┐         │
                    │  │  STATE PATTERN           │         │
                    │  │  state.move(this)        │────────▶│ IdleState
                    │  │  state.addRequest(this,r)│────────▶│ MovingUpState
                    │  │  state.getDirection()    │────────▶│ MovingDownState
                    │  └─────────────────────────┘         │
                    │                                       │
                    │  ┌─────────────────────────┐         │
                    │  │  OBSERVER PATTERN        │         │
                    │  │  notifyObservers()       │────────▶│ ElevatorDisplay
                    │  │  addObserver(obs)        │────────▶│ (future: LogObserver,
                    │  └─────────────────────────┘         │  MobileObserver...)
                    │                                       │
                    │  ┌─────────────────────────┐         │
                    │  │  RUNNABLE (Threading)    │         │
                    │  │  run() → while loop      │────────▶│ ExecutorService
                    │  │  move() every 1 second   │         │ submits this
                    │  └─────────────────────────┘         │
                    └─────────────────────────────────────┘
```

#### 📝 Line-by-Line Summary

| Lines | Section | What It Does |
|-------|---------|-------------|
| 14-18 | **Fields (Identity)** | `id`, `currentFloor`, `state`, `isRunning` — core elevator properties |
| 20-21 | **Fields (Queues)** | `upRequests` (ascending TreeSet), `downRequests` (descending TreeSet) |
| 24 | **Field (Observers)** | List of `ElevatorObserver` for the Observer pattern |
| 26-32 | **Constructor** | Initializes at floor 1, empty queues, IdleState |
| 35-45 | **Observer Methods** | `addObserver()` registers + sends initial state; `notifyObservers()` pushes updates |
| 48-51 | **State Methods** | `setState()` transitions state + notifies; `move()` delegates to state |
| 54-57 | **Request Handling** | `addRequest()` is `synchronized` — delegates routing to the current state |
| 60-76 | **Getters/Setters** | `setCurrentFloor()` also triggers `notifyObservers()` |
| 78-88 | **run() Loop** | The thread heartbeat — calls `move()` every 1 second until shutdown |

#### 💡 Key Design Decisions & Interview Talking Points

| Decision | Rationale |
|----------|-----------|
| **Implements `Runnable`** (not extends `Thread`) | Best practice — allows `ExecutorService` to manage thread lifecycle. Composition over inheritance. |
| **`AtomicInteger` for floor** (not plain `int`) | The strategy reads `getCurrentFloor()` from the main thread while the elevator thread writes it. `AtomicInteger` ensures visibility without locking. |
| **`volatile` for `isRunning`** | The `stopElevator()` is called from the main thread; the `run()` loop reads it from the elevator thread. `volatile` guarantees the write is visible immediately. |
| **`synchronized` on `addRequest()` only** | Minimal locking — only the method that mutates shared TreeSets is synchronized. `move()` doesn't need it because only the elevator's own thread calls it. |
| **Delegates to State, not if-else** | `move()` and `addRequest()` both just call `state.___()`. Zero conditional logic in the Elevator class itself. Adding a new state requires ZERO changes to Elevator. |
| **Two separate TreeSets** (not one PriorityQueue) | The SCAN algorithm needs to serve ALL floors in one direction, then ALL in the other. Two sorted sets make this trivial and O(log n). |
| **Notifies observers in setters** | Every state change or floor change automatically updates the display. The Elevator doesn't know or care what's observing it. |

### ElevatorSystem (Orchestrator)

| Field | Type | Description |
|-------|------|-------------|
| `instance` | `static ElevatorSystem` | Singleton instance |
| `elevators` | `Map<Integer, Elevator>` | All elevators, indexed by ID |
| `selectionStrategy` | `ElevatorSelectionStrategy` | Algorithm for dispatching |
| `executorService` | `ExecutorService` | Thread pool for running elevators |

---

## 🧵 Concurrency Model

```
                    ┌──────────────────────┐
                    │    ExecutorService    │
                    │  (Fixed Thread Pool)  │
                    │     N threads         │
                    └──────┬───────────────┘
                           │ submits
              ┌────────────┼────────────────┐
              ▼            ▼                ▼
       ┌──────────┐  ┌──────────┐    ┌──────────┐
       │Elevator 1│  │Elevator 2│    │Elevator N│
       │ .run()   │  │ .run()   │    │ .run()   │
       │  loop:   │  │  loop:   │    │  loop:   │
       │  move()  │  │  move()  │    │  move()  │
       │  sleep(1s│  │  sleep(1s│    │  sleep(1s│
       └──────────┘  └──────────┘    └──────────┘
```

**Thread-Safety Mechanisms:**

| Mechanism | Where Used | Purpose |
|-----------|-----------|---------|
| `AtomicInteger` | `Elevator.currentFloor` | Lock-free thread-safe floor counter |
| `volatile` | `Elevator.isRunning` | Ensures visibility of shutdown flag across threads |
| `synchronized` | `Elevator.addRequest()` | Prevents race conditions when adding requests |
| `synchronized` | `ElevatorSystem.getInstance()` | Thread-safe singleton initialization |

---

## 🌳 Data Structures — Why TreeSet?

The choice of `TreeSet<Integer>` for request queues is critical to the **SCAN (elevator) algorithm**:

```
  upRequests = new TreeSet<>()              // Natural ascending order
  ─────────────────────────────────────
  Example: {3, 5, 7, 10}
  .first() → 3   (next floor going UP)
  
  downRequests = new TreeSet<>((a,b) -> b-a)  // Reverse (descending) order
  ─────────────────────────────────────
  Example: {8, 5, 3, 1}
  .first() → 8   (next floor going DOWN)
```

| Operation | TreeSet Complexity | Why it matters |
|-----------|-------------------|----------------|
| `add(floor)` | O(log n) | Fast insertion of new requests |
| `first()` | O(log n) | Quickly find the next floor to visit |
| `pollFirst()` | O(log n) | Remove served floor efficiently |
| Auto-dedup | Built-in | If multiple people request floor 5, it's stored once |
| Sorted order | Built-in | Always knows the nearest floor in the current direction |

**Alternative Considered:** `PriorityQueue` — but `TreeSet` provides deduplication and set semantics naturally.

---

## 🚶 Example Walkthrough

Given: 2 elevators, both start at floor 1.

```
TIME   ACTION                           ELEVATOR 1              ELEVATOR 2
─────  ─────────────────────────────    ─────────────────────   ────────────────────
t=0    System starts                    Floor 1, IDLE           Floor 1, IDLE

t=1    External: Floor 5, UP            Strategy picks E1       -
       → E1 gets request                (nearest, idle)
                                        upRequests: {5}

t=2    E1 starts moving                 Floor 2, UP ↑           Floor 1, IDLE
       [DISPLAY] E1 | Floor: 2 | UP

t=3    Internal: E1, floor 10           Floor 3, UP ↑           Floor 1, IDLE
       → E1 gets request                upRequests: {5, 10}

t=4    E1 continues up                  Floor 4, UP ↑           Floor 1, IDLE

t=5    E1 reaches floor 5              Floor 5, UP ↑           Floor 1, IDLE
       → STOPS, opens doors             upRequests: {10}
       → Passenger enters, going to 10

t=6    External: Floor 3, DOWN          Floor 6, UP ↑           Strategy picks E2
       → E2 gets request                                        upRequests: {3}

t=7    E2 starts moving                Floor 7, UP ↑           Floor 2, UP ↑

t=8    Internal: E2, floor 1           Floor 8, UP ↑           Floor 3, UP ↑
       → E2 gets request                                        → STOPS at floor 3
                                                                downRequests: {1}

t=9    E2 reverses direction           Floor 9, UP ↑           Floor 2, DOWN ↓

t=10   E1 reaches floor 10            Floor 10, UP ↑          Floor 1, DOWN ↓
       → STOPS, opens doors             upRequests: {}           → STOPS at floor 1
       → E1 becomes IDLE                                        downRequests: {}
                                        Floor 10, IDLE          Floor 1, IDLE
```

---

## 🔮 Extensibility & Future Enhancements

| Enhancement | Pattern to Use | Description |
|-------------|---------------|-------------|
| **MaintenanceState** | State Pattern | Add a new `ElevatorState` implementation; elevator ignores requests |
| **EmergencyState** | State Pattern | Override `move()` to go directly to ground floor |
| **LeastLoadedStrategy** | Strategy Pattern | New `ElevatorSelectionStrategy` that picks the elevator with fewest pending requests |
| **ZoneBasedStrategy** | Strategy Pattern | Assign elevator 1 to floors 1-10, elevator 2 to floors 11-20 |
| **MobileAppObserver** | Observer Pattern | Push floor/direction updates to a mobile app in real-time |
| **Weight Sensor** | New Component | Add capacity check before accepting more passengers |
| **Door State** | State Pattern | Add `DoorOpenState` / `DoorClosedState` sub-states |
| **Priority Requests** | Model Extension | VIP or emergency requests jump the queue |

---

## 🎯 Interview Talking Points

### 1. "Why did you choose the State Pattern over if-else?"

> "The elevator's behavior fundamentally changes based on its direction. With if-else, adding a new state like `MaintenanceState` would require modifying existing code in multiple places, violating the Open/Closed Principle. The State pattern encapsulates each behavior, making the system extensible and each state independently testable."

### 2. "How does the SCAN algorithm work here?"

> "It's like a disk-scheduling SCAN algorithm. The elevator services all requests in one direction (using a sorted TreeSet), then reverses. This minimizes direction changes and reduces average wait time. The `TreeSet` with natural ordering handles UP, and a reverse-comparator TreeSet handles DOWN."

### 3. "Why Singleton for ElevatorSystem?"

> "A building has exactly one elevator control system. The Singleton ensures we don't accidentally create duplicate systems that conflict with each other. I've used `synchronized` for thread-safe lazy initialization, though in production I'd consider the Bill Pugh idiom or an enum singleton."

### 4. "How do you handle concurrency?"

> "Each elevator runs as a separate thread via `ExecutorService`. I use `AtomicInteger` for lock-free floor updates, `volatile` for the shutdown flag, and `synchronized` on `addRequest()` to prevent race conditions when multiple threads dispatch requests to the same elevator."

### 5. "How would you add a new elevator selection algorithm?"

> "I'd create a new class implementing `ElevatorSelectionStrategy` — say `LeastLoadedStrategy`. Then inject it into `ElevatorSystem` via the constructor or a setter. No existing code needs to change. That's the power of the Strategy pattern."

### 6. "What's the role of the Observer pattern here?"

> "It decouples the elevator from its display. The elevator doesn't know or care what's observing it — it just calls `notifyObservers()`. This lets us add logging, alerts, or mobile push notifications without touching the `Elevator` class."

### 7. "What are the trade-offs of TreeSet vs PriorityQueue?"

> "TreeSet gives us automatic deduplication (no duplicate floor entries), O(log n) for insert/remove/peek, and sorted-set semantics. PriorityQueue allows duplicates and doesn't support efficient removal of arbitrary elements. Since we need unique floor stops in sorted order, TreeSet is the better fit."

### 8. "How would you handle a full-scale production system?"

> "I'd add: (a) persistent request queues for crash recovery, (b) a message broker for inter-service communication, (c) weight/capacity sensors, (d) an API gateway for mobile app integration, (e) comprehensive monitoring and alerting via the Observer pattern, and (f) circuit breakers for fault tolerance."

---

## 📁 Project Structure

```
elevatorsystem/
├── Elevator.java                  # Core entity + Runnable thread
├── ElevatorSystem.java            # Singleton + Facade orchestrator
├── ElevatorSystemDemo.java        # Client/Demo entry point
├── DESIGN.md                      # ← You are here
│
├── enums/
│   ├── Direction.java             # UP, DOWN, IDLE
│   └── RequestSource.java         # INTERNAL, EXTERNAL
│
├── models/
│   └── Request.java               # Request data model
│
├── observer/
│   ├── ElevatorObserver.java      # Observer interface
│   └── ElevatorDisplay.java       # Concrete observer (console display)
│
├── state/
│   ├── ElevatorState.java         # State interface
│   ├── IdleState.java             # No movement, waits for requests
│   ├── MovingUpState.java         # Moving up, serving upRequests
│   └── MovingDownState.java       # Moving down, serving downRequests
│
└── strategy/
    ├── ElevatorSelectionStrategy.java  # Strategy interface
    └── NearestElevatorStrategy.java    # Picks nearest suitable elevator
```

---

## 📊 Pattern Summary

```
┌──────────────────┬────────────────────────┬──────────────────────────────┐
│    Pattern        │    Where Applied        │    Key Benefit               │
├──────────────────┼────────────────────────┼──────────────────────────────┤
│ State            │ ElevatorState +         │ Clean behavior switching     │
│                  │ Idle/MovingUp/Down      │ without if-else chains       │
├──────────────────┼────────────────────────┼──────────────────────────────┤
│ Strategy         │ ElevatorSelection       │ Swappable elevator dispatch  │
│                  │ Strategy + Nearest      │ algorithms                   │
├──────────────────┼────────────────────────┼──────────────────────────────┤
│ Observer         │ ElevatorObserver +      │ Decoupled real-time          │
│                  │ ElevatorDisplay         │ display updates              │
├──────────────────┼────────────────────────┼──────────────────────────────┤
│ Singleton        │ ElevatorSystem          │ Single system instance       │
│                  │ .getInstance()          │ for the building             │
├──────────────────┼────────────────────────┼──────────────────────────────┤
│ Facade           │ ElevatorSystem          │ Simple API hiding            │
│                  │ .requestElevator()      │ internal complexity          │
│                  │ .selectFloor()          │                              │
└──────────────────┴────────────────────────┴──────────────────────────────┘
```

---

*This design document is intended for LLD interview preparation. Practice explaining each pattern with its "why", "how", and "trade-offs".*

