# 🐍🪜 Snake and Ladder Game — Low-Level Design (LLD) Explained

---

## 📌 Problem Statement

> Design a **Snake and Ladder Game** that supports:
> - A configurable board (size, snakes, ladders)
> - Multiple players taking turns
> - Rolling a dice to move
> - Snakes that pull you down, Ladders that push you up
> - A player wins by landing **exactly** on the last square
> - Rolling a **6** grants an extra turn

---

## 🏗️ High-Level Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                  SnakeAndLadderDemo                      │
│                    (Entry Point)                         │
│          Creates game using Game.Builder                 │
└──────────────────────┬──────────────────────────────────┘
                       │ builds
                       ▼
┌─────────────────────────────────────────────────────────┐
│                       Game                               │
│  ┌───────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │   Board    │  │ Queue<Player>│  │      Dice        │  │
│  │           │  │              │  │                  │  │
│  │ size=100  │  │ Alice→Bob→   │  │ min=1, max=6    │  │
│  │ snakes &  │  │   Charlie    │  │ roll() → 1..6   │  │
│  │ ladders   │  │              │  │                  │  │
│  │ (HashMap) │  │  (LinkedList)│  │                  │  │
│  └───────────┘  └──────────────┘  └──────────────────┘  │
│                                                         │
│  status: NOT_STARTED → RUNNING → FINISHED               │
│  winner: Player (set when someone reaches 100)          │
└─────────────────────────────────────────────────────────┘
                       │
                       │ board uses
                       ▼
┌─────────────────────────────────────────────────────────┐
│                    BoardEntity (abstract)                 │
│              fields: start, end                          │
│                 ┌──────┴──────┐                          │
│                 │             │                          │
│            ┌────▼───┐   ┌────▼────┐                     │
│            │ Snake   │   │ Ladder  │                     │
│            │start>end│   │start<end│                     │
│            └────────┘   └─────────┘                     │
└─────────────────────────────────────────────────────────┘
```

---

## 📂 File-by-File Breakdown

### 1️⃣ `enums/GameStatus.java` — Game State Machine

```java
public enum GameStatus {
    NOT_STARTED,   // Game created but play() not called yet
    RUNNING,       // Game loop is active
    FINISHED       // A player reached square 100
}
```

**Why?** Instead of using boolean flags like `isRunning`, `isFinished`, an enum makes state transitions **explicit and self-documenting**. The game loop checks `status == RUNNING` to keep going.

```
  NOT_STARTED ──play()──▶ RUNNING ──player wins──▶ FINISHED
```

---

### 2️⃣ `models/BoardEntity.java` — Abstract Base for Snakes & Ladders

```java
public abstract class BoardEntity {
    private final int start;  // position where entity begins
    private final int end;    // position where entity takes you
}
```

**Key Insight:** Both snakes and ladders are fundamentally the same thing — **a mapping from one square to another**. The only difference:
- **Snake:** `start > end` (moves you DOWN)
- **Ladder:** `start < end` (moves you UP)

This is the **core abstraction** of the design.

---

### 3️⃣ `models/Snake.java` — Validates `start > end`

```java
public class Snake extends BoardEntity {
    public Snake(int start, int end) {
        super(start, end);
        if (start <= end)  // ⛔ Snake MUST go downward
            throw new IllegalArgumentException("Snake head must be higher than tail");
    }
}
```

**Example:** `new Snake(17, 7)` → land on 17, slide down to 7.

---

### 4️⃣ `models/Ladder.java` — Validates `start < end`

```java
public class Ladder extends BoardEntity {
    public Ladder(int start, int end) {
        super(start, end);
        if (start >= end)  // ⛔ Ladder MUST go upward
            throw new IllegalArgumentException("Ladder bottom must be lower than top");
    }
}
```

**Example:** `new Ladder(3, 38)` → land on 3, climb up to 38.

---

### 5️⃣ `models/Board.java` — The Game Board

```java
public class Board {
    private final int size;                          // e.g. 100
    private final Map<Integer, Integer> snakesAndLadders;  // position → destination

    public Board(int size, List<BoardEntity> entities) {
        // Flattens all snakes & ladders into one HashMap
        for (BoardEntity entity : entities)
            snakesAndLadders.put(entity.getStart(), entity.getEnd());
    }

    public int getFinalPosition(int position) {
        // If position has a snake/ladder → return destination
        // Otherwise → return same position (no effect)
        return snakesAndLadders.getOrDefault(position, position);
    }
}
```

**💡 Brilliant Design Choice:** The board doesn't care whether an entity is a Snake or Ladder. It just stores `start → end` mappings. Polymorphism lets both types be treated uniformly through `BoardEntity`.

```
Board's internal HashMap:
  17 → 7   (Snake)     3 → 38  (Ladder)
  54 → 34  (Snake)    24 → 33  (Ladder)
  62 → 19  (Snake)    42 → 93  (Ladder)
  98 → 79  (Snake)    72 → 84  (Ladder)
```

---

### 6️⃣ `models/Dice.java` — Configurable Dice

```java
public class Dice {
    private final int minValue;  // typically 1
    private final int maxValue;  // typically 6

    public int roll() {
        return (int)(Math.random() * (maxValue - minValue + 1) + minValue);
    }
}
```

**Why configurable?** You could easily support a 12-sided dice or any custom range. **Open/Closed Principle** — open for extension without modifying existing code.

---

### 7️⃣ `models/Player.java` — Player State

```java
public class Player {
    private final String name;
    private int position;       // starts at 0 (off-board)

    // getters + setPosition()
}
```

Simple data holder. Position starts at `0` (before the board) and progresses toward `board.size` (100).

---

### 8️⃣ `Game.java` — The Game Engine (⭐ Core Logic)

#### Construction via Builder Pattern:
```java
Game game = new Game.Builder()
    .setBoard(100, boardEntities)   // Step 1: configure board
    .setPlayers(players)            // Step 2: add players
    .setDice(new Dice(1, 6))        // Step 3: set dice
    .build();                       // Step 4: validate & create
```

#### Game Loop — `play()` method:
```
┌──────────────────────────────────────────────────┐
│  status = RUNNING                                 │
│                                                   │
│  while (status == RUNNING):                       │
│    ┌─────────────────────────────────────────┐    │
│    │  currentPlayer = queue.poll()  (dequeue) │    │
│    │  takeTurn(currentPlayer)                 │    │
│    │                                         │    │
│    │  if still RUNNING:                      │    │
│    │    queue.add(currentPlayer)  (re-enqueue)│    │
│    └─────────────────────────────────────────┘    │
│                                                   │
│  Print winner                                     │
└──────────────────────────────────────────────────┘
```

#### Turn Logic — `takeTurn(player)` method:
```
  Roll dice → get number (1-6)
       │
       ▼
  nextPosition = currentPosition + roll
       │
       ├── nextPosition > 100?  → "Turn skipped" (must land exactly)
       │
       ├── nextPosition == 100? → 🏆 WINNER! Set status=FINISHED
       │
       └── nextPosition < 100?
              │
              ▼
         finalPosition = board.getFinalPosition(nextPosition)
              │
              ├── finalPosition > nextPosition → 🪜 Ladder! Climbed up
              ├── finalPosition < nextPosition → 🐍 Snake! Slid down
              └── finalPosition == nextPosition → Normal move
              │
              ▼
         player.setPosition(finalPosition)
              │
              ├── roll == 6? → 🎲 Extra turn! (recursive call)
              └── Otherwise → turn ends
```

---

### 9️⃣ `SnakeAndLadderDemo.java` — Client / Driver

Sets up the game board with:
- **4 Snakes:** (17→7), (54→34), (62→19), (98→79)
- **4 Ladders:** (3→38), (24→33), (42→93), (72→84)
- **3 Players:** Alice, Bob, Charlie
- **Dice:** standard 1-6

```
Board Visualization (simplified):
  100 ── 99 ── 98🐍── 97 ── ...
              ↓(79)
  ...── 93 ── 92 ── ...
        ↑
  ...── 84 ── 83 ── ...
        ↑
  ...── 72🪜── 71 ── ...
  
  ...── 62🐍── 61 ── ...
        ↓(19)
  ...── 54🐍── 53 ── ...
        ↓(34)
  ...── 42🪜── 41 ── ...
        ↑(93)
  ...── 38 ── 37 ── ...
        ↑
  ...── 33 ── 32 ── ...
        ↑
  ...── 24🪜── 23 ── ...
  
  ...── 17🐍── 16 ── ...
        ↓(7)
  ...──  3🪜──  2 ──  1
        ↑(38)
```

---

## 🎨 Design Patterns Used

### 1. ⭐ Builder Pattern (`Game.Builder`)

**Where:** `Game` class has a static inner `Builder` class.

**Problem it solves:** The `Game` object needs a `Board`, `Players`, and `Dice` — all of which have their own complex construction. Passing all this into a single constructor would be ugly and error-prone.

```java
// ❌ WITHOUT Builder — messy, order-dependent, hard to read
Game game = new Game(new Board(100, entities), playerQueue, new Dice(1,6));

// ✅ WITH Builder — clean, readable, self-documenting
Game game = new Game.Builder()
    .setBoard(100, boardEntities)
    .setPlayers(players)
    .setDice(new Dice(1, 6))
    .build();
```

**Why it matters:**
| Benefit | How |
|---|---|
| **Readable construction** | Method names describe what each step does |
| **Validation** | `build()` checks all required fields are set |
| **Immutability** | `Game` constructor is `private` — only Builder can create it |
| **Fluent API** | Each setter returns `this` for chaining |

---

### 2. ⭐ Inheritance + Polymorphism (`BoardEntity` → `Snake`, `Ladder`)

**Where:** `BoardEntity` is abstract; `Snake` and `Ladder` extend it.

**Problem it solves:** Snakes and ladders are structurally identical (start→end mapping) but have different validation rules and semantic meaning.

```
                 BoardEntity (abstract)
                 ┌─────────────┐
                 │ start: int  │
                 │ end: int    │
                 └──────┬──────┘
                   ┌────┴────┐
              ┌────▼──┐  ┌───▼────┐
              │ Snake  │  │ Ladder │
              │s > e ✓ │  │s < e ✓ │
              └───────┘  └────────┘
```

**Why it matters:**
- `Board` accepts `List<BoardEntity>` — it doesn't need to know if it's dealing with Snakes or Ladders
- Each subclass **self-validates** in its constructor (Snake ensures start > end, Ladder ensures start < end)
- **Adding a new entity** (e.g., a "Portal" that teleports you) only requires a new subclass — no changes to Board or Game

**This is the Open/Closed Principle (O in SOLID)** — open for extension, closed for modification.

---

### 3. ⭐ Encapsulation & Separation of Concerns

**Where:** Each class owns exactly one responsibility.

| Class | Single Responsibility |
|---|---|
| `Board` | Knows the board layout (size + snake/ladder mappings) |
| `Dice` | Knows how to generate a random roll |
| `Player` | Knows a player's name and current position |
| `Snake` / `Ladder` | Knows its start/end + validates correctness |
| `Game` | Orchestrates the game loop, turns, and win condition |
| `GameStatus` | Defines the finite states of a game |

**Why it matters:** If you need to change how dice work (e.g., weighted dice), you ONLY modify `Dice.java`. Nothing else changes.

---

### 4. ⭐ Queue-Based Turn Management

**Where:** `Game` uses `Queue<Player>` (backed by `LinkedList`).

```
Turn 1:  [Alice, Bob, Charlie]  → poll Alice → Alice plays → add Alice back
Turn 2:  [Bob, Charlie, Alice]  → poll Bob   → Bob plays   → add Bob back
Turn 3:  [Charlie, Alice, Bob]  → poll Charlie → ...
```

**Why a Queue?**
- **FIFO order** naturally models "take turns in order"
- `poll()` removes the front player, `add()` puts them at the back
- When a player wins, they are simply NOT re-added — clean exit

---

### 5. ⭐ Enum-Based State Machine (`GameStatus`)

**Where:** `GameStatus` enum controls the game loop.

```
   NOT_STARTED ──▶ RUNNING ──▶ FINISHED
       │              │             │
    (created)     (game loop)   (winner found)
```

**Why an Enum instead of booleans?**
```java
// ❌ Booleans — confusing, can have invalid combos
boolean isStarted = true;
boolean isFinished = false;

// ✅ Enum — exactly one state at a time, no invalid combos
GameStatus status = GameStatus.RUNNING;
```

---

### 6. ⭐ Self-Validating Objects (Defensive Programming)

**Where:** `Snake`, `Ladder`, and `Game.Builder.build()`.

```java
// Snake validates itself on creation
new Snake(5, 20);  // 💥 throws IllegalArgumentException
                    // "Snake head must be at a higher position than its tail"

// Builder validates completeness
new Game.Builder().build();  // 💥 throws IllegalStateException
                              // "Board, Players, and Dice must be set"
```

**Why?** Invalid objects can **never exist** in the system. Bugs are caught at construction time, not during gameplay.

---

## 🧠 How to Explain This in an Interview (Quick Script)

> **"The core entities are Board, Player, Dice, Snake, and Ladder. Snake and Ladder both extend an abstract BoardEntity class because they're structurally identical — just a start-to-end mapping — but with different validation rules.**
>
> **The Board flattens all entities into a HashMap<Integer, Integer> so it can resolve any position in O(1). The Game class uses a Queue for turn management and is constructed via the Builder pattern for clean, validated setup.**
>
> **The game loop polls a player from the queue, rolls the dice, checks for snakes/ladders, and re-enqueues the player. If someone lands exactly on 100, the game ends."**

---

## 🔑 Key OOP Principles Demonstrated

| Principle | Where Applied |
|---|---|
| **Abstraction** | `BoardEntity` hides implementation details of Snake/Ladder |
| **Encapsulation** | All fields are `private final`; access via getters only |
| **Inheritance** | `Snake` and `Ladder` extend `BoardEntity` |
| **Polymorphism** | `Board` treats `Snake` and `Ladder` uniformly as `BoardEntity` |
| **SRP** (Single Responsibility) | Each class has exactly one job |
| **OCP** (Open/Closed) | Add new entities without modifying Board or Game |
| **DIP** (Dependency Inversion) | Board depends on abstract `BoardEntity`, not concrete Snake/Ladder |

---

## 🚀 Possible Extensions (Follow-Up Questions)

| Extension | How to Implement |
|---|---|
| **Multiple dice** | Change `Dice` to a list, sum their rolls |
| **Power-ups** | New `BoardEntity` subclass (e.g., `DoubleMove`) |
| **Undo/Redo** | Store move history using **Command Pattern** |
| **Multiplayer over network** | Extract `Game` interface, use **Observer Pattern** for events |
| **Custom board shapes** | Make `Board` an interface with different implementations |
| **Leaderboard / Stats** | Add a `GameResult` class, persist with a repository |

---

## 📊 Class Diagram (UML-Style)

```
┌──────────────────────┐         ┌─────────────────┐
│    «enum»            │         │     Dice         │
│    GameStatus        │         ├─────────────────┤
│──────────────────────│         │ -minValue: int   │
│ NOT_STARTED          │         │ -maxValue: int   │
│ RUNNING              │         ├─────────────────┤
│ FINISHED             │         │ +roll(): int     │
└──────────────────────┘         └────────┬────────┘
                                          │
┌─────────────────────────────────────────┼────────────────────┐
│                    Game                  │                    │
├──────────────────────────────────────────────────────────────┤
│ -board: Board                                                │
│ -players: Queue<Player>                                      │
│ -dice: Dice  ◄───────────────────────────┘                   │
│ -status: GameStatus                                          │
│ -winner: Player                                              │
├──────────────────────────────────────────────────────────────┤
│ +play(): void                                                │
│ -takeTurn(player: Player): void                              │
├──────────────────────────────────────────────────────────────┤
│ «static inner class» Builder                                 │
│   +setBoard(size, entities): Builder                         │
│   +setPlayers(names): Builder                                │
│   +setDice(dice): Builder                                    │
│   +build(): Game                                             │
└──────────┬───────────────────────────────────────────────────┘
           │ has-a
           ▼
┌──────────────────────┐       ┌───────────────────────────┐
│       Board          │       │      «abstract»           │
├──────────────────────┤       │      BoardEntity          │
│ -size: int           │ uses  ├───────────────────────────┤
│ -snakesAndLadders:   │◄──────│ -start: int               │
│   Map<Int, Int>      │       │ -end: int                 │
├──────────────────────┤       ├───────────────────────────┤
│ +getSize(): int      │       │ +getStart(): int          │
│ +getFinalPosition(): │       │ +getEnd(): int            │
│   int                │       └─────────┬─────────────────┘
└──────────────────────┘            ┌────┴────┐
                                    │         │
┌──────────────────┐    ┌───────────▼──┐  ┌───▼──────────┐
│     Player       │    │    Snake     │  │   Ladder     │
├──────────────────┤    ├──────────────┤  ├──────────────┤
│ -name: String    │    │ validates:   │  │ validates:   │
│ -position: int   │    │ start > end  │  │ start < end  │
├──────────────────┤    └──────────────┘  └──────────────┘
│ +getName()       │
│ +getPosition()   │
│ +setPosition()   │
└──────────────────┘
```

---

## 🎯 Sample Game Trace

```
Game started!

Alice's turn. Rolled a 3.
  → position: 0 + 3 = 3
  → Board lookup: 3 → 38 (Ladder! 🪜)
  → Wow! Alice found a ladder at 3 and climbed to 38.

Bob's turn. Rolled a 6.
  → position: 0 + 6 = 6
  → Board lookup: 6 → 6 (no snake/ladder)
  → Bob moved from 0 to 6.
  → Bob rolled a 6 and gets another turn!
  Bob's turn. Rolled a 4.
  → position: 6 + 4 = 10
  → Bob moved from 6 to 10.

Charlie's turn. Rolled a 5.
  → position: 0 + 5 = 5
  → Charlie moved from 0 to 5.

... (game continues) ...

Alice's turn. Rolled a 2.
  → position: 98 + 2 = 100
  → Hooray! Alice reached the final square 100 and won! 🏆

Game Finished!
The winner is Alice!
```

---

*This document was generated to help understand the Snake & Ladder LLD design. Use it alongside the source code for interview preparation.*

