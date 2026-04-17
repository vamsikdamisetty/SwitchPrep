package org.example.pool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core pool implementation — Object Pool + Singleton pattern.
 *
 * Interview talking points:
 * 1. BlockingQueue handles thread-safe hand-off without explicit synchronized.
 * 2. AtomicInteger for totalCount avoids race conditions on pool growth.
 * 3. Eager init to minSize, lazy growth up to maxSize.
 * 4. Singleton via static factory (not enum) for flexibility.
 */
public class BasicConnectionPool implements ConnectionPool {

    private final PoolConfig config;
    private final ConnectionFactory factory;
    private final LinkedBlockingDeque<PooledConnection> idleQueue;
    private final AtomicInteger totalCount;
    private final AtomicBoolean isShutdown;
    private final IdleConnectionEvictor evictor;

    // --- Singleton ---
    private static volatile BasicConnectionPool INSTANCE;

    public static BasicConnectionPool getInstance(PoolConfig config) {
        if (INSTANCE == null) {
            synchronized (BasicConnectionPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BasicConnectionPool(config);
                }
            }
        }
        return INSTANCE;
    }

    // For testing / demo reset
    public static void resetInstance() {
        synchronized (BasicConnectionPool.class) {
            if (INSTANCE != null) {
                INSTANCE.shutdown();
                INSTANCE = null;
            }
        }
    }

    private BasicConnectionPool(PoolConfig config) {
        this.config = config;
        this.factory = new ConnectionFactory();
        this.idleQueue = new LinkedBlockingDeque<>();
        this.totalCount = new AtomicInteger(0);
        this.isShutdown = new AtomicBoolean(false);

        // Eagerly create minSize connections
        System.out.println("Initializing pool with " + config);
        for (int i = 0; i < config.getMinSize(); i++) {
            PooledConnection pc = factory.create();
            idleQueue.offer(pc);
            totalCount.incrementAndGet();
        }
        System.out.println("Pool initialized. Total connections: " + totalCount.get());

        // Start idle connection evictor daemon
        this.evictor = new IdleConnectionEvictor(this, config);
        this.evictor.start();
    }

    @Override
    public PooledConnection getConnection() {
        if (isShutdown.get()) {
            throw new ConnectionPoolException("Pool is shut down!");
        }

        // 1. Try to get an idle connection
        PooledConnection pc = idleQueue.pollFirst();

        // 2. If none available, try to create a new one (up to maxSize)
        if (pc == null) {
            if (totalCount.get() < config.getMaxSize()) {
                // CAS loop to safely increment
                int current;
                do {
                    current = totalCount.get();
                    if (current >= config.getMaxSize()) break;
                } while (!totalCount.compareAndSet(current, current + 1));

                if (current < config.getMaxSize()) {
                    System.out.println("[Pool] Growing pool: " + (current + 1) + "/" + config.getMaxSize());
                    pc = factory.create();
                }
            }
        }

        // 3. If still null, block and wait for a returned connection
        if (pc == null) {
            try {
                System.out.println("[Pool] Max reached. Waiting for available connection...");
                pc = idleQueue.pollFirst(config.getAcquireTimeoutMs(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ConnectionPoolException("Interrupted while waiting for connection", e);
            }
        }

        if (pc == null) {
            throw new ConnectionPoolException("Timeout: could not acquire connection within "
                    + config.getAcquireTimeoutMs() + "ms");
        }

        // Validate before handing out
        if (!pc.isValid()) {
            System.out.println("[Pool] Discarding invalid " + pc);
            totalCount.decrementAndGet();
            return getConnection(); // recursive retry
        }

        pc.markInUse();
        System.out.println("[Pool] Acquired " + pc.getRealConnection());
        return pc;
    }

    @Override
    public void releaseConnection(PooledConnection connection) {
        if (connection == null) return;

        if (isShutdown.get()) {
            connection.destroy();
            totalCount.decrementAndGet();
            return;
        }

        if (!connection.isValid()) {
            System.out.println("[Pool] Discarding invalid " + connection + " on release");
            connection.destroy();
            totalCount.decrementAndGet();
            return;
        }

        connection.markIdle();
        idleQueue.offerLast(connection);
        System.out.println("[Pool] Released " + connection.getRealConnection() + " back to pool");
    }

    @Override
    public void shutdown() {
        if (!isShutdown.compareAndSet(false, true)) return;

        System.out.println("[Pool] Shutting down...");
        evictor.stop();

        PooledConnection pc;
        while ((pc = idleQueue.poll()) != null) {
            pc.destroy();
            totalCount.decrementAndGet();
        }
        System.out.println("[Pool] Shutdown complete. Remaining count: " + totalCount.get());
    }

    @Override
    public int getAvailableCount() { return idleQueue.size(); }

    @Override
    public int getTotalCount() { return totalCount.get(); }

    // Package-private: used by IdleConnectionEvictor
    LinkedBlockingDeque<PooledConnection> getIdleQueue() { return idleQueue; }
    AtomicInteger getTotalCountRef() { return totalCount; }
    ConnectionFactory getFactory() { return factory; }
}

