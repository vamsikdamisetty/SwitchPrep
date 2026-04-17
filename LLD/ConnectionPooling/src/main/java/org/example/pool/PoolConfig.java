package org.example.pool;

/**
 * Immutable configuration for the connection pool.
 * In interviews, explain: "I separate config from logic for SRP and testability."
 */
public class PoolConfig {
    private final int minSize;
    private final int maxSize;
    private final long acquireTimeoutMs;
    private final long idleTimeoutMs;

    private PoolConfig(Builder builder) {
        this.minSize = builder.minSize;
        this.maxSize = builder.maxSize;
        this.acquireTimeoutMs = builder.acquireTimeoutMs;
        this.idleTimeoutMs = builder.idleTimeoutMs;
    }

    public int getMinSize() { return minSize; }
    public int getMaxSize() { return maxSize; }
    public long getAcquireTimeoutMs() { return acquireTimeoutMs; }
    public long getIdleTimeoutMs() { return idleTimeoutMs; }

    public static class Builder {
        private int minSize = 2;
        private int maxSize = 10;
        private long acquireTimeoutMs = 5000;
        private long idleTimeoutMs = 30000;

        public Builder minSize(int val) { this.minSize = val; return this; }
        public Builder maxSize(int val) { this.maxSize = val; return this; }
        public Builder acquireTimeoutMs(long val) { this.acquireTimeoutMs = val; return this; }
        public Builder idleTimeoutMs(long val) { this.idleTimeoutMs = val; return this; }

        public PoolConfig build() {
            if (minSize < 0) throw new IllegalArgumentException("minSize must be >= 0");
            if (maxSize < minSize) throw new IllegalArgumentException("maxSize must be >= minSize");
            if (acquireTimeoutMs <= 0) throw new IllegalArgumentException("acquireTimeoutMs must be > 0");
            if (idleTimeoutMs <= 0) throw new IllegalArgumentException("idleTimeoutMs must be > 0");
            return new PoolConfig(this);
        }
    }

    @Override
    public String toString() {
        return "PoolConfig{min=" + minSize + ", max=" + maxSize +
                ", acquireTimeout=" + acquireTimeoutMs + "ms, idleTimeout=" + idleTimeoutMs + "ms}";
    }
}

