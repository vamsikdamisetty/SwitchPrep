package com.cb.producerconsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =========================================================
 *  PRODUCER
 * =========================================================
 * Responsibility: Generate messages and place them onto the
 * shared BlockingQueue.
 *
 * Key design decisions:
 *  1. Implements Runnable  → can be submitted to any ExecutorService
 *                            or wrapped in a plain Thread.
 *  2. Uses BlockingQueue.put() instead of add() / offer():
 *       - put() BLOCKS when the queue is full, giving us
 *         back-pressure for free — the producer automatically
 *         slows down if consumers can't keep up.
 *  3. AtomicInteger for message IDs → safe to share across
 *     multiple producer threads without explicit locking.
 *  4. A "poison pill" sentinel is sent after all real messages
 *     so consumers know when to stop.
 */
public class Producer implements Runnable {

    // ── Shared state ──────────────────────────────────────────
    /** The shared bounded queue that connects producers to consumers. */
    private final BlockingQueue<Message> queue;

    /** Thread-safe counter shared across all producer instances. */
    private final AtomicInteger messageIdCounter;

    // ── Configuration ─────────────────────────────────────────
    /** How many messages THIS producer instance will generate. */
    private final int messagesToProduce;

    /** Human-readable name for logging (e.g. "Producer-1"). */
    private final String name;

    /**
     * Sentinel / poison-pill message.
     * After finishing its work, each producer posts this special
     * message so that each waiting consumer thread can detect
     * "no more work" and exit cleanly.
     *
     * Using a static final reference allows identity comparison
     * (==) rather than value comparison — very cheap and safe.
     */
    public static final Message POISON_PILL = new Message(-1, "POISON_PILL");

    // ── Constructor ───────────────────────────────────────────
    public Producer(String name,
                    BlockingQueue<Message> queue,
                    AtomicInteger messageIdCounter,
                    int messagesToProduce) {
        this.name             = name;
        this.queue            = queue;
        this.messageIdCounter = messageIdCounter;
        this.messagesToProduce = messagesToProduce;
    }

    // ── Core logic ────────────────────────────────────────────
    @Override
    public void run() {
        try {
            for (int i = 0; i < messagesToProduce; i++) {

                // Atomically get the next global message id.
                // incrementAndGet() is lock-free on modern JVMs (CAS instruction).
                int id = messageIdCounter.incrementAndGet();

                Message msg = new Message(id, "Payload-" + id);

                System.out.printf("[%s] Producing  → %s%n", name, msg);

                // BlockingQueue.put():
                //   • If queue has space   → enqueues immediately, returns.
                //   • If queue is FULL     → blocks (suspends this thread)
                //                            until a consumer removes an element.
                // This is the BACK-PRESSURE mechanism.
                queue.put(msg);

                // Simulate variable production time (e.g., DB read, HTTP call).
                // Remove or tune this in a real system.
                Thread.sleep(50);
            }

            // ── Poison Pill ──────────────────────────────────
            // Signal consumers that this producer is done.
            // Each producer sends exactly ONE poison pill.
            // The Main class ensures the right number of pills
            // matches the number of consumer threads.
            queue.put(POISON_PILL);
            System.out.printf("[%s] Sent POISON_PILL — shutting down.%n", name);

        } catch (InterruptedException e) {
            // Restore the interrupt flag so calling code can react.
            Thread.currentThread().interrupt();
            System.err.printf("[%s] Interrupted!%n", name);
        }
    }
}

