# ATM System — Low-Level Design Document

> **Language:** Java | **Date:** March 14, 2026  
> **Purpose:** Interview prep — Low-Level Design (LLD) walkthrough with design patterns

---

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Project Structure](#2-project-structure)
3. [Class Descriptions](#3-class-descriptions)
4. [Design Patterns](#4-design-patterns)
5. [UML Class Diagram](#5-uml-class-diagram)
6. [State Machine Diagram](#6-state-machine-diagram)
7. [Chain of Responsibility — Cash Dispensing Flow](#7-chain-of-responsibility--cash-dispensing-flow)
8. [Demo Scenarios Walkthrough](#8-demo-scenarios-walkthrough)
9. [Thread Safety](#9-thread-safety)
10. [Key Design Decisions & Trade-offs](#10-key-design-decisions--trade-offs)

---

## 1. System Overview

The ATM system simulates a real-world Automated Teller Machine. A user can:
- **Insert** a card and **authenticate** with a PIN
- **Check balance** of their linked account
- **Withdraw cash** (dispensed in $100, $50, $20 notes)
- **Deposit cash** to their account

The system is built around **3 core design patterns**:

| Pattern | Purpose |
|---|---|
| **Singleton** | One global ATM instance |
| **State** | ATM lifecycle: Idle → HasCard → Authenticated |
| **Chain of Responsibility** | Cash note dispensing ($100 → $50 → $20) |

---

## 2. Project Structure

```
atm/
├── ATMDemo.java                        ← Entry point (main method, 6 scenarios)
├── ATMSystem.java                      ← Core ATM orchestrator (Singleton)
│
├── entities/
│   ├── Account.java                    ← Bank account (balance, deposit, withdraw)
│   ├── Card.java                       ← Debit/credit card (number + PIN)
│   ├── BankService.java                ← In-memory bank (accounts, cards, auth)
│   └── CashDispenser.java              ← Wrapper around the dispense chain
│
├── state/
│   ├── ATMState.java                   ← Interface: all ATM actions
│   ├── IdleState.java                  ← No card inserted
│   ├── HasCardState.java               ← Card inserted, PIN not yet entered
│   └── AuthenticatedState.java         ← PIN verified, can perform operations
│
├── chainofresponsibility/
│   ├── DispenseChain.java              ← Interface for each chain handler
│   ├── NoteDispenser.java              ← Abstract base: dispense logic
│   ├── NoteDispenser100.java           ← Handles $100 notes
│   ├── NoteDispenser50.java            ← Handles $50 notes
│   └── NoteDispenser20.java            ← Handles $20 notes
│
└── enums/
    └── OperationType.java              ← CHECK_BALANCE, WITHDRAW_CASH, DEPOSIT_CASH
```

---

## 3. Class Descriptions

### 3.1 `ATMSystem` (Core Orchestrator)

```
Responsibilities:
- Acts as a Singleton — only one ATM instance exists
- Holds current ATMState and current Card in session
- Delegates user actions (insertCard, enterPin, selectOperation) to current state
- Coordinates BankService and CashDispenser for business logic
- Sets up the dispense chain: NoteDispenser100 → NoteDispenser50 → NoteDispenser20
```

**Key fields:**

| Field | Type | Description |
|---|---|---|
| `INSTANCE` | `ATMSystem` | Static singleton reference |
| `currentState` | `ATMState` | Active ATM state |
| `currentCard` | `Card` | Card in the current session |
| `bankService` | `BankService` | Handles all banking operations |
| `cashDispenser` | `CashDispenser` | Wraps the cash chain |

---

### 3.2 `ATMState` (Interface)

Defines 4 actions that every state must implement:

```java
void insertCard(ATMSystem atm, String cardNumber);
void enterPin(ATMSystem atm, String pin);
void selectOperation(ATMSystem atm, OperationType op, int... args);
void ejectCard(ATMSystem atm);
```

---

### 3.3 `IdleState`

- **Allowed:** `insertCard` → looks up card in BankService; if found → transitions to `HasCardState`
- **Blocked:** `enterPin`, `selectOperation` → prints "Please insert a card first"
- **On invalid card:** calls `ejectCard` → prints "Card not found"

---

### 3.4 `HasCardState`

- **Allowed:** `enterPin` → authenticates with BankService
  - ✅ Correct PIN → transitions to `AuthenticatedState`
  - ❌ Wrong PIN → calls `ejectCard` → transitions back to `IdleState`
- **Blocked:** `insertCard` → "A card is already inserted"
- **Blocked:** `selectOperation` → "Please enter your PIN first"

---

### 3.5 `AuthenticatedState`

- **Allowed:** `selectOperation` handles:
  - `CHECK_BALANCE` → prints formatted balance
  - `WITHDRAW_CASH` → validates amount → delegates to `ATMSystem.withdrawCash()`
  - `DEPOSIT_CASH` → delegates to `ATMSystem.depositCash()`
- After every transaction → automatically calls `ejectCard` → returns to `IdleState`
- **Blocked:** `insertCard`, `enterPin` → prints "Session already active"

---

### 3.6 `BankService`

In-memory bank. Uses `ConcurrentHashMap` for thread safety.

| Method | Description |
|---|---|
| `createAccount()` | Creates and stores an account |
| `createCard()` | Creates and stores a card |
| `linkCardToAccount()` | Maps a card to an account (bidirectional) |
| `authenticate()` | Compares PIN from card object |
| `getBalance()` | Looks up account via card map |
| `withdrawMoney()` | Calls `Account.withdraw()` |
| `depositMoney()` | Calls `Account.deposit()` |

**Pre-loaded data:**

| Card Number | PIN | Account | Initial Balance |
|---|---|---|---|
| `1234-5678-9012-3456` | `1234` | `1234567890` | $1,000 |
| `9876-5432-1098-7654` | `4321` | `9876543210` | $500 |

---

### 3.7 `Account`

- Stores `accountNumber`, `balance`, and linked `cards` map
- `deposit()` and `withdraw()` are **`synchronized`** for thread safety
- `withdraw()` returns `false` if insufficient funds (no exception thrown)

---

### 3.8 `Card`

- Immutable: `cardNumber` and `pin` are `final`
- Simple data holder — no behavior

---

### 3.9 `CashDispenser`

- Wraps the head of the `DispenseChain`
- `canDispenseCash(amount)` — validates amount is a multiple of 10 AND the chain can cover it (dry-run)
- `dispenseCash(amount)` — triggers the chain (synchronized)

---

### 3.10 `NoteDispenser` (Abstract)

Core chain logic:
- Greedily uses as many of its denomination as available
- Passes the remainder to `nextChain`
- Both `dispense()` and `canDispense()` are **`synchronized`**

**Concrete classes:**

| Class | Denomination | Initial Count | Max Value |
|---|---|---|---|
| `NoteDispenser100` | $100 | 10 notes | $1,000 |
| `NoteDispenser50` | $50 | 20 notes | $1,000 |
| `NoteDispenser20` | $20 | 30 notes | $600 |

---

## 4. Design Patterns

---

### 4.1 🔒 Singleton Pattern

**File:** `ATMSystem.java`

**Problem solved:** There should be exactly one ATM instance. Multiple instances would create inconsistent state (different cash levels, different sessions).

**Implementation:**
```
- Private constructor
- Static INSTANCE field
- Public static getInstance() method
- Lazy initialization (created on first call)
```

**Flow:**
```
ATMDemo.main()
    │
    └──▶ ATMSystem.getInstance()
              │
              ├── INSTANCE == null? ──YES──▶ new ATMSystem() ──▶ return INSTANCE
              │
              └── INSTANCE != null? ──▶ return existing INSTANCE
```

> ⚠️ **Trade-off:** Not thread-safe (no double-checked locking). For production, use `synchronized` on `getInstance()` or an `enum`-based singleton.

---

### 4.2 🔄 State Pattern

**Files:** `state/` package

**Problem solved:** ATM behavior changes completely based on its current state. Without this pattern, every method would be filled with `if/else` checks — unmaintainable.

**Participants:**
- **Context:** `ATMSystem` — holds `currentState`, delegates calls to it
- **State Interface:** `ATMState` — defines all possible ATM actions
- **Concrete States:** `IdleState`, `HasCardState`, `AuthenticatedState`

**How state changes happen:**
```
ATMSystem.changeState(new HasCardState())   // called inside IdleState
ATMSystem.changeState(new AuthenticatedState()) // called inside HasCardState
ATMSystem.changeState(new IdleState())       // called inside ejectCard()
```

**Behavior matrix:**

| Action | IdleState | HasCardState | AuthenticatedState |
|---|---|---|---|
| `insertCard` | ✅ Accepts card | ❌ Error | ❌ Error |
| `enterPin` | ❌ Error | ✅ Authenticates | ❌ Error |
| `selectOperation` | ❌ Error | ❌ Error | ✅ Executes |
| `ejectCard` | Resets card | Ejects → Idle | Ends session → Idle |

---

### 4.3 ⛓️ Chain of Responsibility Pattern

**Files:** `chainofresponsibility/` package

**Problem solved:** Cash dispensing requires distributing an amount across multiple denominations. Each denomination handler only knows about its own note value and passes the remainder to the next handler.

**Participants:**
- **Handler Interface:** `DispenseChain` — `setNextChain()`, `dispense()`, `canDispense()`
- **Abstract Handler:** `NoteDispenser` — shared greedy-dispensing logic
- **Concrete Handlers:** `NoteDispenser100` → `NoteDispenser50` → `NoteDispenser20`

**Chain setup in `ATMSystem` constructor:**
```
c1 (NoteDispenser100) ──nextChain──▶ c2 (NoteDispenser50) ──nextChain──▶ c3 (NoteDispenser20)
```

**Example: Withdraw $570**
```
NoteDispenser100: 570 / 100 = 5 notes → dispense 5×$100, remainder = $70
    │
    └──▶ NoteDispenser50: 70 / 50 = 1 note → dispense 1×$50, remainder = $20
              │
              └──▶ NoteDispenser20: 20 / 20 = 1 note → dispense 1×$20, remainder = $0 ✅
```

**Two-phase operation:**
1. `canDispense(amount)` — dry-run to check feasibility (no state change)
2. `dispense(amount)` — actually deducts note counts

---

### 4.4 🪟 Façade Pattern

**File:** `ATMSystem.java` (implicit)

**Problem solved:** `AuthenticatedState` should not need to know about `BankService`, `CashDispenser`, and the chain. `ATMSystem` provides a simplified interface.

```
AuthenticatedState calls:        ATMSystem internally coordinates:
─────────────────────────        ────────────────────────────────
atmSystem.withdrawCash(570)  →   1. cashDispenser.canDispenseCash(570)
                                 2. bankService.withdrawMoney(card, 570)
                                 3. cashDispenser.dispenseCash(570)
                                 4. [rollback] bankService.depositMoney() if step 3 fails
```

---

## 5. UML Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ATM SYSTEM                                     │
└─────────────────────────────────────────────────────────────────────────────┘

        «singleton»
       ┌─────────────────────┐
       │     ATMSystem       │
       ├─────────────────────┤
       │ -INSTANCE           │◄────── getInstance() : ATMSystem
       │ -currentState       │
       │ -currentCard        │──────────────────────────────────┐
       │ -bankService        │──────────────────┐               │
       │ -cashDispenser      │──────┐           │               │
       ├─────────────────────┤      │           │               │
       │ +insertCard()       │      │           │               │
       │ +enterPin()         │      │           │               │
       │ +selectOperation()  │      │           │               │
       │ +changeState()      │      │           │               │
       │ +withdrawCash()     │      │           │               │
       │ +depositCash()      │      │           │               │
       └──────────┬──────────┘      │           │               │
                  │ delegates to    │           │               │
                  ▼                 ▼           ▼               ▼
          «interface»         ┌───────────┐  ┌─────────────┐  ┌──────┐
          ATMState            │CashDispen-│  │ BankService │  │ Card │
          ──────────          │   ser     │  ├─────────────┤  ├──────┤
          insertCard()        ├───────────┤  │ -accounts   │  │ -num │
          enterPin()          │ -chain    │  │ -cards      │  │ -pin │
          selectOperation()   ├───────────┤  │ -cardAccMap │  └──────┘
          ejectCard()         │dispenseCash│ ├─────────────┤
               ▲              │canDispense │ │authenticate │
               │              └─────┬──────┘ │getBalance() │
        ┌──────┼──────┐             │        │withdraw()   │
        │      │      │             │        │deposit()    │
   ┌────┴─┐ ┌──┴───┐ ┌┴────────────┴──────┐ └──────┬──────┘
   │Idle  │ │HasCard│ │AuthenticatedState  │        │
   │State │ │State  │ ├────────────────────┤        ▼
   └──────┘ └───────┘ │selectOperation()  │  ┌─────────────┐
                       │ CHECK_BALANCE     │  │   Account   │
                       │ WITHDRAW_CASH     │  ├─────────────┤
                       │ DEPOSIT_CASH      │  │ -accountNum │
                       └───────────────────┘  │ -balance    │
                                              ├─────────────┤
                                              │ +deposit()  │ ← synchronized
                                              │ +withdraw() │ ← synchronized
                                              └─────────────┘

       «interface»           «abstract»
       DispenseChain ◄──── NoteDispenser ◄──┬── NoteDispenser100
       ─────────────        ─────────────   ├── NoteDispenser50
       setNextChain()       -nextChain      └── NoteDispenser20
       dispense()           -noteValue
       canDispense()        -numNotes
                             setNextChain()
                             dispense()      ← synchronized
                             canDispense()   ← synchronized
```

---

## 6. State Machine Diagram

```
                         ┌─────────────────────────────┐
                         │                             │
                         ▼                             │
                    ┌──────────┐                       │
              ┌────►│   IDLE   │◄──────────────────────┤
              │     │  State   │                       │
              │     └────┬─────┘                       │
              │          │                             │
              │     insertCard(valid)                  │
              │          │                             │
              │          ▼                             │
              │     ┌──────────┐   insertCard(invalid) │
              │     │ HAS CARD │──────────────────────►│(ejectCard)
              │     │  State   │                       │
              │     └────┬─────┘                       │
              │          │                             │
              │    enterPin(wrong) ─────────────────►(ejectCard)→ IDLE
              │          │                             │
              │    enterPin(correct)                   │
              │          │                             │
              │          ▼                             │
              │     ┌──────────────┐                   │
              │     │AUTHENTICATED │                   │
              │     │    State     │                   │
              │     └──────┬───────┘                   │
              │            │                           │
              │      selectOperation()                 │
              │      (after any op)                    │
              │            │                           │
              └────────────┘  ejectCard() ────────────►│
                                                        │
                                               Returns to IDLE
```

**State transition summary:**

```
[IDLE] ────── insertCard(valid) ──────────────────────▶ [HAS CARD]
[IDLE] ────── insertCard(invalid) ────────────────────▶ [IDLE]      (card not found)

[HAS CARD] ── enterPin(correct) ──────────────────────▶ [AUTHENTICATED]
[HAS CARD] ── enterPin(wrong) ────────────────────────▶ [IDLE]      (card ejected)

[AUTHENTICATED] ── selectOperation(any) + done ───────▶ [IDLE]      (auto eject)
[AUTHENTICATED] ── ejectCard() ───────────────────────▶ [IDLE]
```

---

## 7. Chain of Responsibility — Cash Dispensing Flow

```
User requests: WITHDRAW $570
       │
       ▼
ATMSystem.withdrawCash(570)
       │
       ├─ Step 1: CashDispenser.canDispenseCash(570)
       │               │
       │          570 % 10 == 0? ✅
       │               │
       │          chain.canDispense(570) ─── dry-run through chain
       │               │
       │    ┌──────────┴──────────────────────────┐
       │    │  NoteDispenser100: use min(5,10)=5   │
       │    │  remainder = 570 - 500 = 70          │
       │    │        │                             │
       │    │  NoteDispenser50: use min(1,20)=1    │
       │    │  remainder = 70 - 50 = 20            │
       │    │        │                             │
       │    │  NoteDispenser20: use min(1,30)=1    │
       │    │  remainder = 20 - 20 = 0 ✅ OK!      │
       │    └─────────────────────────────────────┘
       │
       ├─ Step 2: bankService.withdrawMoney(card, 570)
       │          Account balance: $1000 → $430
       │
       └─ Step 3: cashDispenser.dispenseCash(570)
                      │
              ┌───────▼─────────────────────────────────┐
              │ NoteDispenser100: Dispense 5×$100 = $500  │
              │   numNotes: 10 → 5, remainder = $70       │
              │         ▼                                 │
              │ NoteDispenser50:  Dispense 1×$50  = $50   │
              │   numNotes: 20 → 19, remainder = $20      │
              │         ▼                                 │
              │ NoteDispenser20:  Dispense 1×$20  = $20   │
              │   numNotes: 30 → 29, remainder = $0  ✅   │
              └─────────────────────────────────────────┘

Output:
  "Dispensing 5 x $100 note(s)"
  "Dispensing 1 x $50 note(s)"
  "Dispensing 1 x $20 note(s)"
```

**Rollback safety:**
```
If dispenseCash() throws an exception AFTER bankService.withdrawMoney():
    → bankService.depositMoney(card, amount)  ← money returned to account
```

---

## 8. Demo Scenarios Walkthrough

### Scenario 1 — Check Balance
```
insertCard("1234-5678-9012-3456")    → IDLE → HAS CARD
enterPin("1234")                      → HAS CARD → AUTHENTICATED (✅ correct)
selectOperation(CHECK_BALANCE)        → "Your current account balance is: $1000.00"
                                      → Auto eject → IDLE
```

### Scenario 2 — Withdraw Cash ($570)
```
insertCard("1234-5678-9012-3456")    → IDLE → HAS CARD
enterPin("1234")                      → AUTHENTICATED
selectOperation(WITHDRAW_CASH, 570)
  → Balance check: $1000 ≥ $570 ✅
  → canDispense(570) ✅
  → Bank deducts: $1000 → $430
  → Dispensed: 5×$100 + 1×$50 + 1×$20
  → Auto eject → IDLE
```

### Scenario 3 — Deposit Cash ($200)
```
insertCard("1234-5678-9012-3456")    → IDLE → HAS CARD
enterPin("1234")                      → AUTHENTICATED
selectOperation(DEPOSIT_CASH, 200)
  → Bank adds: $430 → $630
  → Auto eject → IDLE
```

### Scenario 4 — Check Balance (after deposit)
```
selectOperation(CHECK_BALANCE)        → "Your current account balance is: $630.00"
```

### Scenario 5 — Withdraw More Than Balance ($700)
```
insertCard("1234-5678-9012-3456")    → IDLE → HAS CARD
enterPin("1234")                      → AUTHENTICATED
selectOperation(WITHDRAW_CASH, 700)
  → Balance check: $630 < $700 ❌
  → "Error: Insufficient balance."
  → Auto eject → IDLE
```

### Scenario 6 — Wrong PIN
```
insertCard("1234-5678-9012-3456")    → IDLE → HAS CARD
enterPin("3425")                      → "Authentication failed: Incorrect PIN."
                                      → Card ejected → IDLE
```

---

## 9. Thread Safety

| Component | Mechanism | Reason |
|---|---|---|
| `Account.deposit()` | `synchronized` method | Prevent concurrent balance corruption |
| `Account.withdraw()` | `synchronized` method | Prevent race in balance check + deduct |
| `NoteDispenser.dispense()` | `synchronized` method | Prevent over-dispensing from two sessions |
| `NoteDispenser.canDispense()` | `synchronized` method | Consistent dry-run check |
| `CashDispenser.dispenseCash()` | `synchronized` method | Serializes access to chain |
| `BankService` maps | `ConcurrentHashMap` | Safe concurrent reads/writes |

> ⚠️ **Known gap:** `ATMSystem.getInstance()` is not synchronized. In a multi-threaded environment, two threads could each create a separate instance. Fix: use `synchronized` on the method, or use an `enum` singleton.

---

## 10. Key Design Decisions & Trade-offs

| Decision | Benefit | Trade-off |
|---|---|---|
| **State Pattern** | Clean separation of behavior per state; no messy `if/else` chains | Adding a new state requires implementing all interface methods |
| **Chain of Responsibility** | Easy to add new denominations (e.g., $10 notes) | Order matters — must be set up largest-to-smallest |
| **Greedy dispensing in chain** | Minimizes number of notes used | May fail if only larger denominations are available for a small amount |
| **canDispense() dry-run** | Prevents partial dispensing and data inconsistency | Traverses entire chain twice (once to check, once to dispense) |
| **Façade in ATMSystem** | States stay thin and focused; rollback is centralized | ATMSystem grows as more operations are added |
| **Singleton** | Simple global access; consistent state | Not thread-safe in current form; harder to unit test |
| **In-memory BankService** | No DB dependency for demo | Not persistent; data resets on every run |
| **Auto-eject after transaction** | Simplified session model | Cannot perform multiple operations per session without re-inserting card |

---

## Quick Reference Card

```
┌────────────────────────────────────────────────────────────┐
│                    ATM PATTERNS AT A GLANCE                │
├──────────────────┬─────────────────────────────────────────┤
│ Singleton        │ ATMSystem — one ATM, one instance       │
│ State            │ Idle → HasCard → Authenticated → Idle   │
│ Chain of Resp.   │ $100 → $50 → $20 note dispensing        │
│ Façade           │ ATMSystem hides bank + cash complexity   │
├──────────────────┼─────────────────────────────────────────┤
│ Thread Safety    │ synchronized on Account + NoteDispenser  │
│                  │ ConcurrentHashMap in BankService         │
├──────────────────┼─────────────────────────────────────────┤
│ Pre-loaded Cards │ 1234-5678-9012-3456 / PIN: 1234 / $1000 │
│                  │ 9876-5432-1098-7654 / PIN: 4321 / $500  │
└──────────────────┴─────────────────────────────────────────┘
```

