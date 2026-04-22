package com.java.concurrency;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * ============================================================
 *   VIRTUAL THREADS  vs  PLATFORM (OS) THREADS  –  Demo
 *   Requires Java 21+  (Project Loom – JEP 444)
 * ============================================================
 *
 * KEY DIFFERENCES
 * ─────────────────────────────────────────────────────────────
 * Feature               | Platform Thread          | Virtual Thread
 * ──────────────────────|──────────────────────────|──────────────────────────
 * Backed by             | OS/kernel thread (1:1)   | JVM-managed (M:N on carrier threads)
 * Memory per thread     | ~1 MB stack              | Few KB (grows on demand)
 * Creation cost         | Expensive (OS call)      | Cheap (JVM object)
 * Max practical count   | Thousands                | Millions
 * Scheduling            | OS scheduler             | JVM scheduler (ForkJoinPool)
 * Blocking behavior     | Blocks OS thread         | Unmounts from carrier; carrier reused
 * Best for              | CPU-bound work           | I/O-bound / high-concurrency work
 * Thread pool needed?   | YES                      | NO  (create per-task)
 * ─────────────────────────────────────────────────────────────
 *
 * WHEN TO USE VIRTUAL THREADS
 * • High-concurrency I/O: HTTP servers, DB calls, file I/O, REST clients.
 * • Replace thread-pool-based executors for blocking tasks.
 *
 * WHEN TO STICK WITH PLATFORM THREADS
 * • CPU-intensive work (virtual threads don't add parallelism beyond core count).
 * • Need to set thread priority or use ThreadLocal extensively.
 */
public class VirtualVsPlatformThreads {

    private static final int THREAD_COUNT = 100_000;

    // ====== 1. CREATING THREADS — 3 ways ======

    /** Old way: platform thread */
    static void demoPlatformThread() throws InterruptedException {
        Thread t = Thread.ofPlatform()
                .name("platform-1")
                .start(() -> System.out.printf("[%s] isVirtual=%b%n",
                        Thread.currentThread().getName(),
                        Thread.currentThread().isVirtual()));
        t.join();
    }

    /** New way: virtual thread (unstarted + start) */
    static void demoVirtualThread() throws InterruptedException {
        Thread t = Thread.ofVirtual()
                .name("virtual-1")
                .start(() -> System.out.printf("[%s] isVirtual=%b%n",
                        Thread.currentThread().getName(),
                        Thread.currentThread().isVirtual()));
        t.join();
    }

    /** Convenience factory: Thread.startVirtualThread() */
    static void demoStartVirtualThread() throws InterruptedException {
        Thread t = Thread.startVirtualThread(() ->
                System.out.printf("[%s] isVirtual=%b%n",
                        Thread.currentThread().getName(),
                        Thread.currentThread().isVirtual()));
        t.join();
    }

    // ====== 2. EXECUTOR SERVICES ======

    /** Platform thread pool — bounded, reuses threads */
    static void demoFixedThreadPool() throws Exception {
        try (ExecutorService exec = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 5; i++) {
                int id = i;
                exec.submit(() -> System.out.printf("  FixedPool task-%d on %s%n",
                        id, Thread.currentThread().getName()));
            }
        } // auto-shutdown on close (Java 19+)
    }

    /** Virtual thread-per-task executor — creates a new virtual thread per task */
    static void demoVirtualPerTaskExecutor() throws Exception {
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                int id = i;
                exec.submit(() -> System.out.printf("  VirtualPerTask task-%d on %s (virtual=%b)%n",
                        id, Thread.currentThread().getName(),
                        Thread.currentThread().isVirtual()));
            }
        }
    }

    // ====== 3. SCALABILITY BENCHMARK — spawn 100k threads ======

    static void benchmarkPlatformThreads() {
        Instant start = Instant.now();
        // Using a fixed pool because creating 100k OS threads would crash
        try (ExecutorService exec = Executors.newFixedThreadPool(200)) {
            IntStream.range(0, THREAD_COUNT).forEach(i ->
                    exec.submit(() -> {
                        try { Thread.sleep(10); } catch (InterruptedException e) { }
                    }));
        }
        Duration elapsed = Duration.between(start, Instant.now());
        System.out.printf("  Platform (pool=200) : %d tasks in %d ms%n",
                THREAD_COUNT, elapsed.toMillis());
    }

    static void benchmarkVirtualThreads() {
        Instant start = Instant.now();
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, THREAD_COUNT).forEach(i ->
                    exec.submit(() -> {
                        try { Thread.sleep(10); } catch (InterruptedException e) { }
                    }));
        }
        Duration elapsed = Duration.between(start, Instant.now());
        System.out.printf("  Virtual threads     : %d tasks in %d ms%n",
                THREAD_COUNT, elapsed.toMillis());
    }

    // ====== 4. BLOCKING BEHAVIOR — virtual threads unmount from carrier ======

    static void demoBlockingBehavior() throws InterruptedException {
        Thread vt = Thread.ofVirtual().name("blocker").start(() -> {
            System.out.println("  [blocker] before sleep — running on carrier");
            try {
                Thread.sleep(100); // virtual thread unmounts; carrier is FREE
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("  [blocker] after sleep — may be on a DIFFERENT carrier");
        });
        vt.join();
    }

    // ====== MAIN ======
    public static void main(String[] args) throws Exception {

        System.out.println("═══ Demo 1: Creating Threads ═══");
        demoPlatformThread();
        demoVirtualThread();
        demoStartVirtualThread();

        System.out.println("\n═══ Demo 2: Executor Services ═══");
        System.out.println("-- FixedThreadPool (platform) --");
        demoFixedThreadPool();
        System.out.println("-- VirtualThreadPerTaskExecutor --");
        demoVirtualPerTaskExecutor();

        System.out.println("\n═══ Demo 3: Scalability — " + THREAD_COUNT + " tasks ═══");
        benchmarkPlatformThreads();
        benchmarkVirtualThreads();

        System.out.println("\n═══ Demo 4: Blocking Behavior ═══");
        demoBlockingBehavior();

        System.out.println("\nAll demos complete.");
    }

    /*
    When a virtual thread hits a blocking I/O call, it does NOT block the OS thread

    Instead:

    JVM parks the virtual thread
    Frees the underlying OS thread
    Runs another virtual thread on that OS thread

    If you use:

    synchronized
    native calls
    blocking operations not Loom-friendly

    👉 Virtual thread can get pinned to OS thread

    → loses scalability advantage

    Better:

    Use ReentrantLock instead of synchronized

     */
}

