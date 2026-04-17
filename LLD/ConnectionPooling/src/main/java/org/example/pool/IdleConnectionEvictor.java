package org.example.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Background daemon that evicts idle connections and replenishes to minSize.
 * Interview: "Prevents resource leaks and keeps the pool healthy."
 */
public class IdleConnectionEvictor {

    private final BasicConnectionPool pool;
    private final PoolConfig config;
    private ScheduledExecutorService scheduler;

    public IdleConnectionEvictor(BasicConnectionPool pool, PoolConfig config) {
        this.pool = pool;
        this.config = config;
    }

    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "idle-evictor");
            t.setDaemon(true);
            return t;
        });

        long checkInterval = Math.max(config.getIdleTimeoutMs() / 2, 1000);
        scheduler.scheduleAtFixedRate(this::evict, checkInterval, checkInterval, TimeUnit.MILLISECONDS);
        System.out.println("[Evictor] Started. Check interval: " + checkInterval + "ms");
    }

    private void evict() {
        var idleQueue = pool.getIdleQueue();
        var totalCount = pool.getTotalCountRef();

        // Drain idle queue, keep non-expired, evict expired
        List<PooledConnection> keep = new ArrayList<>();
        PooledConnection pc;
        while ((pc = idleQueue.poll()) != null) {
            if (pc.getIdleTimeMs() > config.getIdleTimeoutMs() && totalCount.get() > config.getMinSize()) {
                System.out.println("[Evictor] Evicting idle " + pc.getRealConnection());
                pc.destroy();
                totalCount.decrementAndGet();
            } else {
                keep.add(pc);
            }
        }
        // Put back the ones we're keeping
        for (PooledConnection kept : keep) {
            idleQueue.offerLast(kept);
        }

        // Replenish to minSize if needed
        while (totalCount.get() < config.getMinSize()) {
            System.out.println("[Evictor] Replenishing pool to minSize");
            PooledConnection fresh = pool.getFactory().create();
            idleQueue.offerLast(fresh);
            totalCount.incrementAndGet();
        }
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            System.out.println("[Evictor] Stopped");
        }
    }
}

