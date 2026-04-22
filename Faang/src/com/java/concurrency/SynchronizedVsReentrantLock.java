package com.java.concurrency;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ============================================================
 * SYNCHRONIZED  vs  ReentrantLock  –  Side-by-Side Demo
 * ============================================================
 * <p>
 * KEY DIFFERENCES
 * ─────────────────────────────────────────────────────────────
 * Feature                  | synchronized        | ReentrantLock
 * ─────────────────────────|─────────────────────|──────────────────────────
 * Automatic release        | YES (JVM handles)   | NO  (must call unlock())
 * Try to acquire (no wait) | NO                  | YES (tryLock())
 * Timed lock attempt       | NO                  | YES (tryLock(time, unit))
 * Interruptible lock wait  | NO                  | YES (lockInterruptibly())
 * Fairness policy          | NO (unfair only)    | YES (new ReentrantLock(true))
 * Multiple conditions      | ONE (wait/notify)   | MANY (newCondition())
 * Lock inspection          | Limited             | Rich (isLocked, getQueueLength…)
 * ─────────────────────────────────────────────────────────────
 * <p>
 * WHEN TO PREFER EACH
 * • synchronized  → simple critical sections, less boilerplate, harder to misuse.
 * • ReentrantLock → need tryLock / timeout / fairness / multiple conditions.
 */
public class SynchronizedVsReentrantLock {

    private int syncCounter = 0;
    private int lockCounter = 0;
    private final ReentrantLock lock = new ReentrantLock();

    // ====== 1. SYNCHRONIZED — JVM auto-acquires & releases the intrinsic lock ======
    public synchronized void incrementWithSynchronized() {
        syncCounter++;
        System.out.printf("[%s] synchronized → counter = %d%n",
                Thread.currentThread().getName(), syncCounter);
    }

    // ====== 2. REENTRANTLOCK — must manually lock/unlock (always in finally!) ======
    public void incrementWithLock() {
        lock.lock();
        try {
            lockCounter++;
            System.out.printf("[%s] ReentrantLock → counter = %d%n",
                    Thread.currentThread().getName(), lockCounter);
        } finally {
            lock.unlock();
        }
    }

    // ====== 3. tryLock() — non-blocking attempt (ReentrantLock ONLY) ======
    public void incrementWithTryLock() {
        if (lock.tryLock()) {
            try {
                lockCounter++;
                System.out.printf("[%s] tryLock succeeded → counter = %d%n",
                        Thread.currentThread().getName(), lockCounter);
            } finally {
                lock.unlock();
            }
        } else {
            System.out.printf("[%s] tryLock FAILED – lock held by another thread%n",
                    Thread.currentThread().getName());
        }
    }

    // ====== 4. REENTRANCY — both allow same thread to re-acquire its own lock ======
    public synchronized void outerSync() {
        System.out.println("[Reentrant-sync] outerSync acquired lock");
        innerSync();  // same thread re-acquires same intrinsic lock – OK
    }

    private synchronized void innerSync() {
        System.out.println("[Reentrant-sync] innerSync also holds lock – no deadlock");
    }

    public void outerLock() {
        lock.lock();
        try {
            System.out.printf("[Reentrant-lock] hold count = %d%n", lock.getHoldCount());
            innerLock();
        } finally {
            lock.unlock();
        }
    }

    private void innerLock() {
        lock.lock();
        try {
            System.out.printf("[Reentrant-lock] hold count inside innerLock = %d%n",
                    lock.getHoldCount());
        } finally {
            lock.unlock();
        }
    }

    // ====== MAIN ======
    public static void main(String[] args) throws InterruptedException {

        SynchronizedVsReentrantLock demo = new SynchronizedVsReentrantLock();

        // --- Demo 1: basic synchronized vs ReentrantLock ---
        System.out.println("═══ Demo 1: synchronized vs ReentrantLock ═══");
        Thread[] t1 = new Thread[3];
        for (int i = 0; i < 3; i++) {
            t1[i] = new Thread(demo::incrementWithSynchronized, "Sync-" + i);
            t1[i].start();
        }
        for (Thread t : t1) t.join();

        System.out.println();
        Thread[] t2 = new Thread[3];
        for (int i = 0; i < 3; i++) {
            t2[i] = new Thread(demo::incrementWithLock, "Lock-" + i);
            t2[i].start();
        }
        for (Thread t : t2) t.join();

        // --- Demo 2: tryLock (non-blocking) ---
        System.out.println("\n═══ Demo 2: tryLock (non-blocking) ═══");
        Thread holder = new Thread(() -> {
            demo.lock.lock();
            try {
                System.out.println("[Holder] holding lock for 200ms …");
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                demo.lock.unlock();
                System.out.println("[Holder] released lock");
            }
        }, "Holder");
        holder.start();
        Thread.sleep(50);
        Thread c1 = new Thread(demo::incrementWithTryLock, "Contender-1");
        c1.start();
        holder.join();
        c1.join();

        // --- Demo 3: reentrancy ---
        /*
        Both support reentrancy identically in behavior.
        The difference is that ReentrantLock gives you visibility (getHoldCount()) and
         responsibility (manual unlock per lock call), while synchronized handles everything silently.
         */
        System.out.println("\n═══ Demo 3: Reentrancy ═══");
        demo.outerSync();
        System.out.println();
        demo.outerLock();

        // --- Demo 4: ReentrantLock diagnostics ---
        System.out.println("\n═══ Demo 4: Lock Diagnostics (ReentrantLock only) ═══");
        demo.lock.lock();
        try {
            System.out.println("isLocked              = " + demo.lock.isLocked());
            System.out.println("isHeldByCurrentThread  = " + demo.lock.isHeldByCurrentThread());
            System.out.println("holdCount              = " + demo.lock.getHoldCount());
            System.out.println("isFair                 = " + demo.lock.isFair());
        } finally {
            demo.lock.unlock();
        }
        System.out.println("isLocked after unlock  = " + demo.lock.isLocked());

        System.out.println("\nAll demos complete.");
    }
}

