package org.example.pool;

/**
 * Wrapper around a dummy Connection — tracks usage state and timestamps.
 * Key interview point: "Proxy pattern — close() returns to pool instead of destroying."
 */
public class PooledConnection {
    private final Connection realConnection;
    private boolean inUse;
    private long lastAccessTime;
    private final long createdTime;

    public PooledConnection(Connection realConnection) {
        this.realConnection = realConnection;
        this.inUse = false;
        this.createdTime = System.currentTimeMillis();
        this.lastAccessTime = this.createdTime;
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public boolean isInUse() { return inUse; }

    public void markInUse() {
        this.inUse = true;
        this.lastAccessTime = System.currentTimeMillis();
    }

    public void markIdle() {
        this.inUse = false;
        this.lastAccessTime = System.currentTimeMillis();
    }

    public long getIdleTimeMs() {
        return inUse ? 0 : System.currentTimeMillis() - lastAccessTime;
    }

    public long getLastAccessTime() { return lastAccessTime; }
    public long getCreatedTime() { return createdTime; }

    public boolean isValid() {
        return realConnection.isOpen();
    }

    public void destroy() {
        realConnection.close();
    }

    @Override
    public String toString() {
        return "Pooled[" + realConnection + ", inUse=" + inUse + "]";
    }
}

