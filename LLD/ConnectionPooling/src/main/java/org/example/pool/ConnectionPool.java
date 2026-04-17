package org.example.pool;

/**
 * Public API for the connection pool.
 * Interview: "Coding to interface, not implementation — easy to swap/mock."
 */
public interface ConnectionPool {

    /** Acquire a connection (blocks up to acquireTimeoutMs). */
    PooledConnection getConnection();

    /** Return a connection back to the pool. */
    void releaseConnection(PooledConnection connection);

    /** Gracefully shut down the pool, closing all connections. */
    void shutdown();

    /** Current number of available (idle) connections. */
    int getAvailableCount();

    /** Total connections managed (idle + in-use). */
    int getTotalCount();
}

