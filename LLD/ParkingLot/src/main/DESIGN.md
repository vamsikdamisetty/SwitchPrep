# 🅿️ Parking Lot System — Low-Level Design Document

---

## 1. Problem Statement

Design and implement a **Parking Lot Management System** that can:
- Manage a multi-floor parking lot with different spot sizes.
- Park and unpark vehicles of various types (Bike, Car, Truck).
- Issue tickets on entry and calculate fees on exit.
- Support pluggable strategies for spot allocation and fee calculation.

---

## 2. Functional Requirements

| # | Requirement | Status |
|---|------------|--------|
| FR-1 | The parking lot shall support **multiple floors**, each with its own set of spots. | ✅ |
| FR-2 | Each floor shall have spots of different sizes — **SMALL, MEDIUM, LARGE**. | ✅ |
| FR-3 | The system shall support different vehicle types — **Bike (SMALL), Car (MEDIUM), Truck (LARGE)**. | ✅ |
| FR-4 | A vehicle should only be parked in a compatible spot (e.g., a Bike in a SMALL spot, a Car in MEDIUM or LARGE, a Truck only in LARGE). | ✅ |
| FR-5 | On parking, the system shall generate a **ParkingTicket** with a unique ID and entry timestamp. | ✅ |
| FR-6 | On unparking, the system shall record the exit timestamp and **calculate a fee**. | ✅ |
| FR-7 | The fee calculation strategy shall be **interchangeable** (flat rate, vehicle-based, etc.). | ✅ |
| FR-8 | The spot-finding strategy shall be **interchangeable** (best-fit, nearest-first, farthest-first). | ✅ |
| FR-9 | If no compatible spot is available, the system shall reject the parking request gracefully. | ✅ |
| FR-10 | The system shall display **floor-wise availability** grouped by spot size. | ✅ |

## 3. Non-Functional Requirements

| # | Requirement | How It's Addressed |
|---|------------|--------------------|
| NFR-1 | **Thread-safety** | `ConcurrentHashMap` for active tickets & floor spots; `synchronized` on spot operations and `getInstance()`. |
| NFR-2 | **Single point of control** | Singleton `ParkingLot` ensures one global instance. |
| NFR-3 | **Extensibility** | Strategy interfaces for fee & parking; abstract `Vehicle` for new types. |
| NFR-4 | **Loose coupling** | Classes depend on interfaces (`FeeStrategy`, `ParkingStrategy`), not concrete implementations. |

---

## 4. Core Entities & Class Design

### 4.1 Package Structure

```
parkinglot/
├── ParkingLot.java              ← Singleton façade (entry point)
├── ParkingLotDemo.java          ← Client / Driver
├── entities/
│   ├── ParkingFloor.java        ← Manages spots on one floor
│   ├── ParkingSpot.java         ← Single parking spot
│   └── ParkingTicket.java       ← Issued on vehicle entry
├── vehicle/
│   ├── Vehicle.java             ← Abstract base class
│   ├── VehicleSize.java         ← Enum: SMALL, MEDIUM, LARGE
│   ├── Bike.java                ← Concrete vehicle (SMALL)
│   ├── Car.java                 ← Concrete vehicle (MEDIUM)
│   └── Truck.java               ← Concrete vehicle (LARGE)
└── strategy/
    ├── fee/
    │   ├── FeeStrategy.java             ← Interface
    │   ├── FlatRateFeeStrategy.java     ← $10/hr flat
    │   └── VehicleBasedFeeStrategy.java ← Rate varies by size
    └── parking/
        ├── ParkingStrategy.java         ← Interface
        ├── BestFitStrategy.java         ← Smallest fitting spot across all floors
        ├── NearestFirstStrategy.java    ← First available spot, lowest floor first
        └── FarthestFirstStrategy.java   ← First available spot, highest floor first
```

### 4.2 Class Diagram (Textual)

```
┌──────────────────────────────────────────────┐
│              <<Singleton>>                   │
│               ParkingLot                     │
│──────────────────────────────────────────────│
│ - instance: ParkingLot          [static]     │
│ - floors: List<ParkingFloor>                 │
│ - activeTickets: Map<String, ParkingTicket>  │
│ - feeStrategy: FeeStrategy                   │
│ - parkingStrategy: ParkingStrategy           │
│──────────────────────────────────────────────│
│ + getInstance(): ParkingLot     [static]     │
│ + addFloor(ParkingFloor)                     │
│ + setFeeStrategy(FeeStrategy)                │
│ + setParkingStrategy(ParkingStrategy)        │
│ + parkVehicle(Vehicle): Optional<Ticket>     │
│ + unparkVehicle(String): Optional<Double>    │
└──────────────────────────────────────────────┘
         │ has many              uses ─────────────────┐
         ▼                                             ▼
┌─────────────────────────┐          ┌─────────────────────────────┐
│      ParkingFloor       │          │    <<interface>>             │
│─────────────────────────│          │      ParkingStrategy        │
│ - floorNumber: int      │          │─────────────────────────────│
│ - spots: Map<String,    │          │ + findSpot(floors, vehicle) │
│         ParkingSpot>    │          │     : Optional<ParkingSpot> │
│─────────────────────────│          └─────────────────────────────┘
│ + addSpot(ParkingSpot)  │                 △ implements
│ + findAvailableSpot()   │          ┌──────┼──────────────┐
│ + displayAvailability() │          │      │              │
└─────────────────────────┘     BestFit  Nearest    Farthest
         │ has many              Strategy  First       First
         ▼
┌──────────────────────────┐
│      ParkingSpot         │         ┌─────────────────────────────┐
│──────────────────────────│         │    <<interface>>             │
│ - spotId: String         │         │       FeeStrategy           │
│ - spotSize: VehicleSize  │         │─────────────────────────────│
│ - isOccupied: boolean    │         │ + calculateFee(ticket)      │
│ - parkedVehicle: Vehicle │         │     : double                │
│──────────────────────────│         └─────────────────────────────┘
│ + parkVehicle(Vehicle)   │                △ implements
│ + unparkVehicle()        │           ┌────┴──────────┐
│ + canFitVehicle(Vehicle) │       FlatRate      VehicleBased
│ + isAvailable()          │       FeeStrategy   FeeStrategy
└──────────────────────────┘

┌──────────────────────┐         ┌──────────────────────────┐
│   ParkingTicket      │         │   <<abstract>> Vehicle   │
│──────────────────────│         │──────────────────────────│
│ - ticketId: String   │         │ - licenseNumber: String  │
│ - vehicle: Vehicle   │         │ - size: VehicleSize      │
│ - spot: ParkingSpot  │         │──────────────────────────│
│ - entryTimestamp     │         │ + getLicenseNumber()     │
│ - exitTimestamp      │         │ + getSize()              │
│──────────────────────│         └──────────────────────────┘
│ + setExitTimestamp() │                △ extends
│ + getters...         │          ┌─────┼──────┐
└──────────────────────┘         Bike  Car  Truck

         ┌──────────────┐
         │  <<enum>>    │
         │  VehicleSize │
         │──────────────│
         │  SMALL       │    ← Bike
         │  MEDIUM      │    ← Car
         │  LARGE       │    ← Truck
         └──────────────┘
```

---

## 5. Design Patterns Used

### 5.1 🔒 Singleton Pattern — `ParkingLot`

**Intent:** Ensure only one instance of the parking lot exists application-wide.

**How it's applied:**
- `ParkingLot` has a `private` constructor.
- A `private static` field `instance` holds the single object.
- `getInstance()` is `synchronized` to handle concurrent access safely.

```java
private static ParkingLot instance;

private ParkingLot() { ... }

public static synchronized ParkingLot getInstance() {
    if (instance == null) {
        instance = new ParkingLot();
    }
    return instance;
}
```

**Why:** A physical parking lot is a single shared resource. The Singleton prevents conflicting state from multiple instances.

---

### 5.2 🔄 Strategy Pattern — Fee Calculation & Spot Allocation

**Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable at runtime.

**Applied in two dimensions:**

#### A. Fee Calculation Strategy

| Interface | `FeeStrategy` |
|-----------|---------------|
| Method | `double calculateFee(ParkingTicket)` |

| Implementation | Logic |
|----------------|-------|
| `FlatRateFeeStrategy` | Flat **$10/hour** regardless of vehicle type. |
| `VehicleBasedFeeStrategy` | **$10/hr** (SMALL), **$20/hr** (MEDIUM), **$30/hr** (LARGE). |

#### B. Parking Spot Allocation Strategy

| Interface | `ParkingStrategy` |
|-----------|-------------------|
| Method | `Optional<ParkingSpot> findSpot(List<ParkingFloor>, Vehicle)` |

| Implementation | Logic |
|----------------|-------|
| `BestFitStrategy` | Scans **all** floors, picks the **smallest compatible spot** (tightest fit). |
| `NearestFirstStrategy` | Iterates floors **lowest → highest**, returns the **first** available compatible spot. |
| `FarthestFirstStrategy` | Iterates floors **highest → lowest**, returns the **first** available compatible spot. |

**Why:** Parking lots in the real world may need different pricing models (weekday vs weekend, peak hours, loyalty discounts) and allocation policies. The Strategy pattern lets you swap these at runtime via `setFeeStrategy()` / `setParkingStrategy()` without modifying the `ParkingLot` class.

---

### 5.3 🧬 Inheritance & Polymorphism — Vehicle Hierarchy

**Intent:** Use an abstract base class to define common behavior, with concrete subclasses for each vehicle type.

```
Vehicle (abstract)
├── Bike   → size = SMALL
├── Car    → size = MEDIUM
└── Truck  → size = LARGE
```

- `Vehicle` encapsulates `licenseNumber` and `VehicleSize`.
- Subclasses simply call `super(licenseNumber, VehicleSize.XYZ)`.
- All parking logic works with the `Vehicle` abstraction — it never needs to know the concrete type.

**Why:** Adding a new vehicle type (e.g., `Van`, `Bus`) requires only a new subclass — no changes to existing code (Open/Closed Principle).

---

## 6. Key Algorithms

### 6.1 Vehicle-to-Spot Compatibility (`ParkingSpot.canFitVehicle`)

| Vehicle Size | Compatible Spot Sizes |
|-------------|----------------------|
| SMALL (Bike) | SMALL only |
| MEDIUM (Car) | MEDIUM, LARGE |
| LARGE (Truck) | LARGE only |

A MEDIUM vehicle can use a LARGE spot (larger spot accommodates it), but a LARGE vehicle cannot squeeze into a MEDIUM spot.

### 6.2 Fee Calculation

Both strategies use the formula:

```
hours = ⌊(exitTimestamp - entryTimestamp) / 3,600,000⌋ + 1    // ceiling behavior
fee   = hours × rate
```

The `+1` ensures even sub-hour parking is charged for at least one hour.

---

## 7. Concurrency Considerations

| Component | Mechanism | Purpose |
|-----------|-----------|---------|
| `ParkingLot.getInstance()` | `synchronized` method | Safe lazy initialization of singleton |
| `ParkingLot.activeTickets` | `ConcurrentHashMap` | Thread-safe ticket storage for concurrent park/unpark |
| `ParkingFloor.spots` | `ConcurrentHashMap` | Thread-safe spot registry |
| `ParkingFloor.findAvailableSpot()` | `synchronized` method | Prevents two vehicles from claiming the same spot |
| `ParkingSpot.parkVehicle()` / `unparkVehicle()` / `isAvailable()` | `synchronized` methods | Atomic state transitions for each spot |

---

## 8. Flow of Operations

### 8.1 Park Vehicle Flow

```
Client                ParkingLot              ParkingStrategy           ParkingFloor            ParkingSpot
  │                       │                        │                        │                       │
  │── parkVehicle(v) ────►│                        │                        │                       │
  │                       │── findSpot(floors,v) ──►│                        │                       │
  │                       │                        │── findAvailableSpot(v)─►│                       │
  │                       │                        │                        │── canFitVehicle(v) ───►│
  │                       │                        │                        │◄── true/false ─────────│
  │                       │                        │◄── Optional<Spot> ─────│                       │
  │                       │◄── Optional<Spot> ─────│                        │                       │
  │                       │                        │                        │                       │
  │                       │── spot.parkVehicle(v) ──────────────────────────────────────────────────►│
  │                       │── new ParkingTicket(v, spot) ──┐                │                       │
  │                       │── activeTickets.put(license, t)│                │                       │
  │◄── Optional<Ticket> ──│                                │                │                       │
```

### 8.2 Unpark Vehicle Flow

```
Client                ParkingLot              FeeStrategy               ParkingSpot
  │                       │                        │                        │
  │── unparkVehicle(lic)─►│                        │                        │
  │                       │── activeTickets.remove()                        │
  │                       │── ticket.setExitTimestamp()                     │
  │                       │── spot.unparkVehicle() ────────────────────────►│
  │                       │                        │                        │
  │                       │── calculateFee(ticket)─►│                        │
  │                       │◄── fee (double) ────────│                        │
  │◄── Optional<Double> ──│                        │                        │
```

---

## 9. SOLID Principles Adherence

| Principle | How It's Applied |
|-----------|-----------------|
| **S** — Single Responsibility | Each class has one job: `ParkingSpot` manages spot state, `ParkingFloor` manages a collection of spots, `ParkingTicket` holds ticket data, strategies handle algorithms. |
| **O** — Open/Closed | New vehicle types, fee strategies, or parking strategies can be added without modifying existing code — just add a new class implementing the interface. |
| **L** — Liskov Substitution | Any `Vehicle` subclass (Bike, Car, Truck) can be used wherever `Vehicle` is expected. Any `FeeStrategy` implementation is interchangeable. |
| **I** — Interface Segregation | `FeeStrategy` and `ParkingStrategy` are small, focused interfaces with a single method each. |
| **D** — Dependency Inversion | `ParkingLot` depends on `FeeStrategy` and `ParkingStrategy` interfaces, not on concrete implementations. |

---

## 10. How to Extend

| Extension | Steps Required |
|-----------|---------------|
| **New vehicle type** (e.g., `Van`) | 1. Add `VehicleSize` value if needed. 2. Create `Van extends Vehicle`. 3. Update `canFitVehicle()` mapping if new size rules apply. |
| **New fee strategy** (e.g., `TimeBased` with peak-hour surcharge) | 1. Create class implementing `FeeStrategy`. 2. Call `parkingLot.setFeeStrategy(new TimeBased())`. |
| **New parking strategy** (e.g., `RandomStrategy`) | 1. Create class implementing `ParkingStrategy`. 2. Call `parkingLot.setParkingStrategy(new RandomStrategy())`. |
| **Entry/Exit gates** | Add `Gate` entity that interacts with `ParkingLot.parkVehicle()` / `unparkVehicle()`. |
| **Payment system** | Add a `PaymentStrategy` interface (Strategy pattern again) with Credit Card, Cash, UPI implementations. |

---

## 11. Demo Walkthrough (`ParkingLotDemo.java`)

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Create 2 floors with 5 spots total (1S, 2M, 1L on F1; 2M on F2) | Lot initialized |
| 2 | Set `VehicleBasedFeeStrategy` | Fee rates: $10(S), $20(M), $30(L)/hr |
| 3 | Park Bike `B-123` | Assigned to `F1-S1` (SMALL spot) |
| 4 | Park Car `C-456` | Assigned to `F1-M1` (MEDIUM spot, best fit) |
| 5 | Park Truck `T-789` | Assigned to `F1-L1` (LARGE spot) |
| 6 | Park Car `C-999` | F1 MEDIUM full → goes to `F2-M1` |
| 7 | Park Bike `B-000` | No SMALL spots left → **rejected** |
| 8 | Unpark Car `C-456` | Fee calculated, `F1-M1` freed |
| 9 | Display availability | F1: 0S, 1M, 0L free; F2: 1M free |

---

## 12. Summary

This Parking Lot system demonstrates a clean, extensible low-level design using:

| Pattern | Where | Purpose |
|---------|-------|---------|
| **Singleton** | `ParkingLot` | Single global parking lot instance |
| **Strategy** | `FeeStrategy`, `ParkingStrategy` | Interchangeable algorithms for fee calculation & spot allocation |
| **Inheritance/Polymorphism** | `Vehicle` hierarchy | Extensible vehicle types with shared interface |
| **Enum** | `VehicleSize` | Type-safe size classification |

The design follows **SOLID principles**, supports **thread-safety** for concurrent operations, and is **easily extensible** for new vehicle types, pricing models, and allocation strategies.

