# 🏧 ATM System — Low-Level Design Summary

## 📌 Overview

A fully functional ATM simulation implementing two classic design patterns:
- **State Pattern** — models ATM lifecycle (Idle → Card Inserted → Authenticated)
- **Chain of Responsibility Pattern** — models note dispensing ($100 → $50 → $10)
- **Singleton Pattern** — ensures a single shared ATM instance

The system supports **Withdraw**, **Deposit**, and **Check Balance** operations with proper state transitions and cash dispensing logic.

---

## 📦 Package Structure

```
src/main/java/
├── client/          → ATMDemo.java           (Entry point)
├── constants/       → OperationType.java     (Enum: WITHDRAWAL, DEPOSIT, CHECK_BALANCE)
├── entity/          → Account.java, Card.java (Domain models)
├── repository/      → BankService.java, CashDispenser.java
├── dispenser/       → DispenseChain (interface), NotesDispenser (abstract), 
│                      NotesDispenser100, NotesDispenser50, NotesDispenser10
├── state/           → ATMState (interface), IdleState, InsertedState, AuthenticatedState
└── service/         → ATMSystem.java         (Core singleton orchestrator)
```

---

## 🗂️ Class Diagram

```mermaid
classDiagram
    class ATMSystem {
        -ATMSystem instance$
        -BankService bankRepo
        -String accountId
        -String cardId
        -ATMState currentState
        -CashDispenser cashDispenser
        +getInstance() ATMSystem$
        +insertCard(cardId)
        +enterPin(pin)
        +selectOperation(op, args)
        +withdrawCash(amount)
        +depositCash(amount)
        +getAccountBalance() int
        +moveState(ATMState)
        +validateCard(cardId) bool
        +authenticate(pin) bool
        +resetSystem()
    }

    class ATMState {
        <<interface>>
        +insertCard(ATMSystem, cardId)
        +enterPin(ATMSystem, pin)
        +selectOperation(ATMSystem, OperationType, args)
        +eject(ATMSystem)
    }

    class IdleState {
        +insertCard(ATMSystem, cardId)
        +enterPin() ❌ not allowed
        +selectOperation() ❌ not allowed
        +eject(ATMSystem)
    }

    class InsertedState {
        +insertCard() ❌ not allowed
        +enterPin(ATMSystem, pin)
        +selectOperation() ❌ not allowed
        +eject(ATMSystem)
    }

    class AuthenticatedState {
        +insertCard() ❌ not allowed
        +enterPin() ❌ not allowed
        +selectOperation(ATMSystem, op, args)
        +eject(ATMSystem)
    }

    class BankService {
        -Map~String,Account~ accounts
        -Map~String,Card~ cards
        +getAccountId(cardId) String
        +authenticate(cardId, pin) bool
        +getAccountBalance(accId) int
        +withDraw(accountId, amount)
        +deposit(accountId, amount)
    }

    class CashDispenser {
        -DispenseChain chain
        +canDispense(amount) bool
        +dispense(amount)
    }

    class DispenseChain {
        <<interface>>
        +setNextChain(DispenseChain)
        +canDispense(amount) bool
        +dispense(amount)
    }

    class NotesDispenser {
        <<abstract>>
        -DispenseChain nextChain
        -int noteValue
        -int numNotes
        +setNextChain(DispenseChain)
        +canDispense(amount) bool
        +dispense(amount)
    }

    class NotesDispenser100 { +NotesDispenser100(numNotes) }
    class NotesDispenser50  { +NotesDispenser50(numNotes)  }
    class NotesDispenser10  { +NotesDispenser10(numNotes)  }

    class Account {
        -String accountId
        -String name
        -int balance
        +getBalance() int
        +setBalance(int)
    }

    class Card {
        -String cardId
        -String pin
        -String accountId
        +getAccountId() String
        +getPin() String
    }

    class OperationType {
        <<enumeration>>
        WITHDRAWL
        DEPOSIT
        CHECK_BALANCE
    }

    ATMSystem --> ATMState : currentState
    ATMSystem --> BankService
    ATMSystem --> CashDispenser
    ATMState <|.. IdleState
    ATMState <|.. InsertedState
    ATMState <|.. AuthenticatedState
    CashDispenser --> DispenseChain : chain
    DispenseChain <|.. NotesDispenser
    NotesDispenser <|-- NotesDispenser100
    NotesDispenser <|-- NotesDispenser50
    NotesDispenser <|-- NotesDispenser10
    BankService --> Account
    BankService --> Card
    AuthenticatedState ..> OperationType
```

---

## 🔄 State Machine Diagram

```mermaid
stateDiagram-v2
    [*] --> IdleState : ATM powered on

    IdleState --> InsertedState : insertCard() — valid card
    IdleState --> IdleState : insertCard() — invalid card (eject)

    InsertedState --> AuthenticatedState : enterPin() — correct PIN
    InsertedState --> IdleState : enterPin() — wrong PIN (eject)

    AuthenticatedState --> IdleState : selectOperation(WITHDRAW) — success → eject
    AuthenticatedState --> IdleState : selectOperation(DEPOSIT)  — success → eject
    AuthenticatedState --> IdleState : selectOperation(CHECK_BALANCE) → eject
    AuthenticatedState --> IdleState : selectOperation() — invalid → eject

    IdleState --> [*] : ATM shutdown
```

---

## ⛓️ Chain of Responsibility — Cash Dispensing

```mermaid
flowchart LR
    A["CashDispenser\n(entry point)"] --> B["NotesDispenser100\n(20 × $100)"]
    B -->|remaining amount| C["NotesDispenser50\n(30 × $50)"]
    C -->|remaining amount| D["NotesDispenser10\n(50 × $10)"]
    D -->|remaining amount| E["❌ Cannot dispense\n(return false)"]

    style A fill:#4a90d9,color:#fff
    style B fill:#27ae60,color:#fff
    style C fill:#f39c12,color:#fff
    style D fill:#e74c3c,color:#fff
    style E fill:#7f8c8d,color:#fff
```

**Example — Dispensing $570:**
| Denomination | Notes Used | Amount Covered |
|---|---|---|
| $100 | 5 | $500 |
| $50  | 1 | $50  |
| $10  | 2 | $20  |
| **Total** | **8 notes** | **$570** |

---

## 🔁 Sequence Diagram — Withdraw Flow

```mermaid
sequenceDiagram
    actor User
    participant ATMDemo
    participant ATMSystem
    participant IdleState
    participant InsertedState
    participant AuthenticatedState
    participant BankService
    participant CashDispenser

    User->>ATMDemo: Run main()
    ATMDemo->>ATMSystem: getInstance()
    ATMSystem-->>ATMDemo: singleton instance

    ATMDemo->>ATMSystem: insertCard("1234-5678-9012-3456")
    ATMSystem->>IdleState: insertCard(atm, cardId)
    IdleState->>BankService: getAccountId(cardId)
    BankService-->>IdleState: "ACC1"
    IdleState->>ATMSystem: moveState(InsertedState)

    ATMDemo->>ATMSystem: enterPin("1234")
    ATMSystem->>InsertedState: enterPin(atm, "1234")
    InsertedState->>BankService: authenticate(cardId, pin)
    BankService-->>InsertedState: true
    InsertedState->>ATMSystem: moveState(AuthenticatedState)

    ATMDemo->>ATMSystem: selectOperation(WITHDRAWL, 570)
    ATMSystem->>AuthenticatedState: selectOperation(atm, WITHDRAWL, 570)
    AuthenticatedState->>BankService: getAccountBalance("ACC1")
    BankService-->>AuthenticatedState: 1000
    AuthenticatedState->>ATMSystem: withdrawCash(570)
    ATMSystem->>BankService: getAccountBalance("ACC1")
    BankService-->>ATMSystem: 1000
    ATMSystem->>CashDispenser: canDispense(570)
    CashDispenser-->>ATMSystem: true
    ATMSystem->>BankService: withDraw("ACC1", 570)
    ATMSystem->>CashDispenser: dispense(570)
    CashDispenser-->>User: Dispensing 5×$100, 1×$50, 2×$10
    AuthenticatedState->>ATMSystem: eject()
    ATMSystem->>ATMSystem: resetSystem() + moveState(IdleState)
```

---

## 🎨 Design Patterns Used

| Pattern | Where Applied | Purpose |
|---|---|---|
| **Singleton** | `ATMSystem` | One shared ATM instance (thread-safe with `synchronized`) |
| **State** | `ATMState`, `IdleState`, `InsertedState`, `AuthenticatedState` | Enforces valid operation sequences; prevents invalid actions |
| **Chain of Responsibility** | `DispenseChain`, `NotesDispenser100/50/10` | Greedy note dispensing across denominations |

---

## 🧩 Key Design Decisions

### 1. Singleton — `ATMSystem`
```java
public synchronized static ATMSystem getInstance() {
    if (instance == null) {
        instance = new ATMSystem();
    }
    return instance;
}
```
- `synchronized` ensures thread safety for the first instantiation.
- The ATM holds a single `currentState` reference that gets swapped as the user progresses.

### 2. State Pattern
- Each state (`IdleState`, `InsertedState`, `AuthenticatedState`) only permits specific operations.
- Invalid operations simply print `"Operation not allowed"` — no exceptions thrown, no if-else chains in `ATMSystem`.
- `ATMSystem.moveState()` delegates state transitions cleanly.

### 3. Chain of Responsibility — Note Dispensing
```
c1 ($100) → c2 ($50) → c3 ($10)
```
- `canDispense()` traverses the chain *before* actually dispensing — acts as a pre-check guard.
- `dispense()` greedily uses the highest denomination first, passes remainder down.
- If dispensing throws after the bank debit, `ATMSystem` rolls back via `bankRepo.deposit()`.

### 4. Thread Safety on `CashDispenser`
```java
public synchronized boolean canDispense(int amount) { ... }
public synchronized void dispense(int amount) { ... }
```
- Prevents two concurrent sessions from dispensing the same physical notes.

---

## 🗃️ Entity Model

```mermaid
erDiagram
    CARD {
        string cardId PK
        string pin
        string accountId FK
    }
    ACCOUNT {
        string accountId PK
        string name
        int balance
    }
    CARD }o--|| ACCOUNT : "linked to"
```

---

## 🚀 How to Run

```bash
# From project root
mvn compile
mvn exec:java -Dexec.mainClass="client.ATMDemo"
```

**Expected Output (partial):**
```
Card Validated Successfully
Pin validated Successfully
Account Balance: 1000
Transaction complete.
Ending session. Card has been ejected. Thank you for using our ATM.

Card Validated Successfully
Pin validated Successfully
Processing withdrawal for $570
Dispensing 5 x $100 note(s)
Dispensing 1 x $50 note(s)
Dispensing 2 x $10 note(s)
Transaction complete.
Ending session. Card has been ejected. Thank you for using our ATM.
```

---

## ⚠️ Error Scenarios Handled

| Scenario | Handling |
|---|---|
| Invalid card number | `IdleState` ejects immediately |
| Wrong PIN | `InsertedState` ejects, resets |
| Insufficient account balance | `AuthenticatedState` prints error, ejects |
| ATM out of cash | `CashDispenser.canDispense()` returns false |
| Dispensing exception after debit | `ATMSystem` rolls back via `deposit()` |
| Operation in wrong state | State returns `"Operation not allowed"` |

---

*Generated: March 15, 2026*

