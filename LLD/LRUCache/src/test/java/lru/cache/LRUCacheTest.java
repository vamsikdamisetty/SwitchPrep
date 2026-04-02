package lru.cache;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    // ─── Basic get / put ─────────────────────────────────────────────

    @Test
    void get_returnsNullForMissingKey() {
        Cache<String, String> cache = new LRUCache<>(3);
        assertNull(cache.get("missing"));
    }

    @Test
    void put_thenGet_returnsValue() {
        Cache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        cache.put("b", 2);

        assertEquals(1, cache.get("a"));
        assertEquals(2, cache.get("b"));
    }

    @Test
    void put_overwriteExistingKey_updatesValue() {
        Cache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        cache.put("a", 42);

        assertEquals(42, cache.get("a"));
        assertEquals(1, cache.size(), "Size should not increase on overwrite");
    }

    // ─── Size ────────────────────────────────────────────────────────

    @Test
    void size_reflectsEntryCount() {
        Cache<Integer, Integer> cache = new LRUCache<>(5);
        assertEquals(0, cache.size());

        cache.put(1, 100);
        cache.put(2, 200);
        assertEquals(2, cache.size());
    }

    // ─── Eviction ────────────────────────────────────────────────────

    @Test
    void eviction_removesLeastRecentlyUsed() {
        Cache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        // Cache is full: [b(MRU), a(LRU)]

        cache.put("c", 3); // Should evict "a"

        assertNull(cache.get("a"), "LRU item 'a' should have been evicted");
        assertEquals(2, cache.get("b"));
        assertEquals(3, cache.get("c"));
        assertEquals(2, cache.size());
    }

    @Test
    void eviction_respectsAccessOrder() {
        Cache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        // Access-order: [b(MRU), a(LRU)]

        cache.get("a"); // Promote "a" → [a(MRU), b(LRU)]

        cache.put("c", 3); // Should evict "b" (now LRU)

        assertNull(cache.get("b"), "'b' should have been evicted");
        assertEquals(1, cache.get("a"));
        assertEquals(3, cache.get("c"));
    }

    @Test
    void eviction_overwritePromotesAndDoesNotEvict() {
        Cache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        // [b(MRU), a(LRU)]

        cache.put("a", 10); // Overwrite promotes "a" → [a(MRU), b(LRU)]

        cache.put("c", 3); // Evicts "b"

        assertNull(cache.get("b"), "'b' should have been evicted after 'a' was promoted by overwrite");
        assertEquals(10, cache.get("a"));
        assertEquals(3, cache.get("c"));
    }

    // ─── Edge cases ──────────────────────────────────────────────────

    @Test
    void capacityOfOne_evictsImmediately() {
        Cache<String, Integer> cache = new LRUCache<>(1);
        cache.put("a", 1);
        cache.put("b", 2);

        assertNull(cache.get("a"));
        assertEquals(2, cache.get("b"));
        assertEquals(1, cache.size());
    }

    @Test
    void constructor_rejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<>(-1));
    }

    // ─── Concurrency stress test ─────────────────────────────────────

    @Test
    void concurrency_noExceptionsAndSizeNeverExceedsCapacity() throws Exception {
        final int capacity = 50;
        final int threadCount = 100;
        final int opsPerThread = 1_000;
        Cache<Integer, Integer> cache = new LRUCache<>(capacity);

        // Barrier ensures all threads start at roughly the same time
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < threadCount; t++) {
            futures.add(executor.submit(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                for (int i = 0; i < opsPerThread; i++) {
                    int key = rng.nextInt(200); // key space larger than capacity
                    if (rng.nextBoolean()) {
                        cache.put(key, key * 10);
                    } else {
                        cache.get(key);
                    }
                }
            }));
        }

        // Wait for all threads to finish and propagate any exception
        for (Future<?> f : futures) {
            f.get(30, TimeUnit.SECONDS);
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertTrue(cache.size() <= capacity,
                "Cache size (" + cache.size() + ") should never exceed capacity (" + capacity + ")");
    }
}

