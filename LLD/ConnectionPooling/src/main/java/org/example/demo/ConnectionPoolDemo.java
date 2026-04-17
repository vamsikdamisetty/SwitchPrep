package org.example.demo;

import org.example.pool.*;

/**
 * Multi-threaded demo showcasing:
 * 1. Pool initialization (eager minSize)
 * 2. Concurrent acquire/release
 * 3. Pool growth up to maxSize
 * 4. Timeout when pool exhausted
 */
public class ConnectionPoolDemo {

    public static void main(String[] args) throws InterruptedException {

        // --- Setup ---
        PoolConfig config = new PoolConfig.Builder()
                .minSize(2)
                .maxSize(4)
                .acquireTimeoutMs(3000)
                .idleTimeoutMs(10000)
                .build();

        ConnectionPool pool = BasicConnectionPool.getInstance(config);

        System.out.println("\n=== Demo 1: Basic acquire & release ===");
        PooledConnection c1 = pool.getConnection();
        c1.getRealConnection().execute("SELECT * FROM users");
        pool.releaseConnection(c1);

        System.out.println("\n=== Demo 2: Concurrent access (5 threads, max 4 connections) ===");
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    System.out.println("[Thread-" + threadId + "] Requesting connection...");
                    PooledConnection conn = pool.getConnection();
                    conn.getRealConnection().execute("Query from thread " + threadId);
                    Thread.sleep(500); // simulate work
                    pool.releaseConnection(conn);
                    System.out.println("[Thread-" + threadId + "] Done.");
                } catch (ConnectionPoolException e) {
                    System.out.println("[Thread-" + threadId + "] FAILED: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "worker-" + i);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("\n=== Pool stats ===");
        System.out.println("Available: " + pool.getAvailableCount());
        System.out.println("Total:     " + pool.getTotalCount());

        System.out.println("\n=== Demo 3: Shutdown ===");
        pool.shutdown();

        // Reset singleton for clean re-runs
        BasicConnectionPool.resetInstance();
    }
}

