package com.cb.producerconsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =========================================================
 *  ProducerConsumerMain — Coordinator / Entry Point
 * =========================================================
 *
 * PATTERN OVERVIEW
 * ────────────────
 *  Producer-Consumer (also called Bounded Buffer) decouples
 *  the rate of PRODUCING data from the rate of CONSUMING it.
 *
 *  Producers ──► [  Shared Bounded Queue  ] ──► Consumers
 *                 (BlockingQueue<Message>)
 *
 *  Benefits:
 *   • Producers never wait for consumers to be ready (async).
 *   • Consumers never spin-poll; they sleep when queue is empty.
 *   • Back-pressure: producers slow down when the queue is full.
 *   • Easy to scale: add more producer or consumer threads.
 *
 * THREAD COORDINATION (how we avoid data races)
 * ──────────────────────────────────────────────
 *  • java.util.concurrent.BlockingQueue handles ALL the locking
 *    internally (ReentrantLock + Conditions in LinkedBlockingQueue).
 *  • AtomicInteger for global message ID counter → lock-free CAS.
 *  • ExecutorService manages thread lifecycle → no manual
 *    Thread.start() / Thread.join() boilerplate.
 *
 * SHUTDOWN PROTOCOL (Poison Pill)
 * ────────────────────────────────
 *  1. Each producer thread sends ONE poison pill when it finishes.
 *  2. Main gathers all poison pills and re-distributes exactly
 *     NUM_CONSUMERS pills so every consumer gets one and stops.
 *
 *  Why not use queue.shutdown()?
 *   → BlockingQueue has no built-in "close" signal.
 *     Poison pill is the canonical Java approach.
 *
 * SCENARIO IN THIS DEMO
 * ──────────────────────
 *   2 Producers  × 10 messages each  = 20 total messages
 *   3 Consumers  (slower than producers → back-pressure kicks in)
 *   Queue capacity = 5  (very small to make back-pressure visible)
 */
public class ProducerConsumerMain {

    // ── Tunable parameters ────────────────────────────────────
    private static final int NUM_PRODUCERS        = 2;
    private static final int NUM_CONSUMERS        = 3;
    private static final int MESSAGES_PER_PRODUCER = 10;
    private static final int QUEUE_CAPACITY       = 5;  // bounded!

    public static void main(String[] args) throws InterruptedException {

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Producer-Consumer Demo                 ║");
        System.out.printf ("║   Producers=%d  Consumers=%d  QCap=%d      ║%n",
                            NUM_PRODUCERS, NUM_CONSUMERS, QUEUE_CAPACITY);
        System.out.println("╚══════════════════════════════════════════╝\n");

        // ── 1. Create the shared queue ────────────────────────
        // LinkedBlockingQueue:
        //   • FIFO ordering (fair, easy to reason about).
        //   • Bounded by QUEUE_CAPACITY — put() will BLOCK producers
        //     when the queue is full (back-pressure).
        //   • Uses TWO separate locks: one for put, one for take,
        //     giving higher concurrency than ArrayBlockingQueue.
        BlockingQueue<Message> sharedQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

        // ── 2. Shared atomic counter for globally unique IDs ──
        // AtomicInteger.incrementAndGet() is a single CAS instruction
        // on x86/ARM — no synchronized block needed.
        AtomicInteger messageIdCounter = new AtomicInteger(0);

        // ── 3. Create consumer instances (keep references for metrics) ──
        List<Consumer> consumerInstances = new ArrayList<>();
        for (int i = 1; i <= NUM_CONSUMERS; i++) {
            consumerInstances.add(new Consumer("Consumer-" + i, sharedQueue));
        }

        // ── 4. Launch consumer threads FIRST ──────────────────
        // Consumers start waiting on the empty queue (take() blocks).
        // It's safe to start them before producers because take()
        // simply parks the thread until work arrives.
        ExecutorService consumerPool =
            Executors.newFixedThreadPool(NUM_CONSUMERS);
        for (Consumer c : consumerInstances) {
            consumerPool.submit(c);
        }

        // ── 5. Launch producer threads ────────────────────────
        // Each producer is independent: it generates its own batch
        // of messages and sends ONE poison pill when done.
        ExecutorService producerPool =
            Executors.newFixedThreadPool(NUM_PRODUCERS);
        for (int i = 1; i <= NUM_PRODUCERS; i++) {
            Producer p = new Producer(
                "Producer-" + i,
                sharedQueue,
                messageIdCounter,
                MESSAGES_PER_PRODUCER
            );
            producerPool.submit(p);
        }

        // ── 6. Wait for ALL producers to finish ───────────────
        // shutdown()  → no new tasks accepted, but queued ones run.
        // awaitTermination() → blocks until all tasks complete OR timeout.
        producerPool.shutdown();
        boolean producersDone =
            producerPool.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("\n[Main] All producers finished: " + producersDone);

        // ── 7. Drain poison pills from queue, re-add per consumer ──
        // At this point NUM_PRODUCERS poison pills are in the queue.
        // We want exactly NUM_CONSUMERS pills so every consumer exits.
        // Strategy: drain the producers' pills, then add consumer-count pills.
        //
        // Simpler alternative (used here):
        //   Just add (NUM_CONSUMERS - NUM_PRODUCERS) extra pills if needed,
        //   or unconditionally add NUM_CONSUMERS pills after draining.
        int pillsInQueue = 0;
        List<Message> drainBuffer = new ArrayList<>();
        sharedQueue.drainTo(drainBuffer);  // grab everything remaining

        for (Message m : drainBuffer) {
            if (m == Producer.POISON_PILL) {
                pillsInQueue++;
            } else {
                // Real messages that hadn't been consumed yet — re-queue them
                // so consumers can still process them.
                sharedQueue.put(m);
            }
        }
        System.out.printf("[Main] Found %d poison pill(s) from producers.%n",
                          pillsInQueue);

        // Now send exactly NUM_CONSUMERS poison pills so every consumer exits.
        System.out.printf("[Main] Sending %d poison pill(s) for consumers.%n",
                          NUM_CONSUMERS);
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            sharedQueue.put(Producer.POISON_PILL);
        }

        // ── 8. Wait for all consumers to finish ───────────────
        consumerPool.shutdown();
        boolean consumersDone =
            consumerPool.awaitTermination(60, TimeUnit.SECONDS);
        System.out.println("[Main] All consumers finished: " + consumersDone);

        // ── 9. Final summary ──────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║              SUMMARY                 ║");
        System.out.println("╠══════════════════════════════════════╣");
        int total = 0;
        for (Consumer c : consumerInstances) {
            System.out.printf("║  %-12s processed: %3d messages  ║%n",
                              /* name not stored in Consumer — using index */
                              "Consumer", c.getProcessedCount());
            total += c.getProcessedCount();
        }
        int expected = NUM_PRODUCERS * MESSAGES_PER_PRODUCER;
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf( "║  Total processed : %3d / %3d expected ║%n",
                           total, expected);
        System.out.println("╚══════════════════════════════════════╝");
    }
}

