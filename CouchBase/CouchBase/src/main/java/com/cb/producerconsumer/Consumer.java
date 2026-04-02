package com.cb.producerconsumer;

import java.util.concurrent.BlockingQueue;

/**
 * =========================================================
 *  CONSUMER
 * =========================================================
 * Responsibility: Continuously pull messages from the shared
 * BlockingQueue and process them.
 *
 * Key design decisions:
 *  1. Implements Runnable  → submitted to an ExecutorService
 *                            just like the Producer.
 *  2. Uses BlockingQueue.take() instead of poll():
 *       - take() BLOCKS when the queue is EMPTY, suspending
 *         this thread with zero CPU usage until a message
 *         arrives.  This avoids busy-waiting/spin-loops.
 *  3. Stops when it receives the POISON_PILL sentinel.
 *     The coordinator (Main) puts one poison pill per
 *     consumer thread, guaranteeing every consumer exits.
 */
public class Consumer implements Runnable {

    // ── Shared state ──────────────────────────────────────────
    /** The same shared queue injected into all Producers and Consumers. */
    private final BlockingQueue<Message> queue;

    // ── Configuration ─────────────────────────────────────────
    /** Human-readable name for logging (e.g. "Consumer-1"). */
    private final String name;

    // ── Metrics ───────────────────────────────────────────────
    /** Counts how many messages this specific consumer processed. */
    private int processedCount = 0;

    // ── Constructor ───────────────────────────────────────────
    public Consumer(String name, BlockingQueue<Message> queue) {
        this.name  = name;
        this.queue = queue;
    }

    // ── Core logic ────────────────────────────────────────────
    @Override
    public void run() {
        try {
            // Loop forever until we receive the poison pill.
            while (true) {

                // BlockingQueue.take():
                //   • If queue has a message → dequeues and returns it immediately.
                //   • If queue is EMPTY      → blocks (thread parks) until a
                //                              producer enqueues something.
                // No CPU is wasted while waiting — the OS wakes this thread
                // only when there is real work.
                Message msg = queue.take();

                // ── Poison Pill check ─────────────────────────
                // Identity comparison (==) is intentional and fast.
                // We placed the POISON_PILL as a static final reference,
                // so there is exactly ONE object we are checking against.
                if (msg == Producer.POISON_PILL) {
                    System.out.printf("[%s] Received POISON_PILL — stopping. " +
                                      "Total processed: %d%n", name, processedCount);
                    // Exit the loop → run() returns → thread finishes cleanly.
                    break;
                }

                // ── Simulate processing ───────────────────────
                // In a real system this could be:
                //   - Writing to a database
                //   - Calling a downstream REST API
                //   - Transforming / enriching the message
                processMessage(msg);
                processedCount++;
            }
        } catch (InterruptedException e) {
            // Restore interrupt flag and exit gracefully.
            Thread.currentThread().interrupt();
            System.err.printf("[%s] Interrupted! Processed so far: %d%n",
                              name, processedCount);
        }
    }

    /**
     * Simulates processing work for a single message.
     *
     * @param msg the message to process
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    private void processMessage(Message msg) throws InterruptedException {
        long latency = System.currentTimeMillis() - msg.getCreatedAt();
        System.out.printf("[%s] Consuming   ← %s  (queue-latency: %d ms)%n",
                          name, msg, latency);

        // Simulate variable processing time (slower than producer → causes
        // the queue to fill up and trigger back-pressure on the producer side).
        Thread.sleep(120);
    }

    /** Exposed so Main can print a final summary. */
    public int getProcessedCount() {
        return processedCount;
    }
}

