package lru.cache;

/**
 * A generic cache interface.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public interface Cache<K, V> {

    /**
     * Retrieves the value for the given key.
     * Returns {@code null} if the key does not exist.
     * Moves the item to the "most recently used" position.
     *
     * @param key the key whose associated value is to be returned
     * @return the value, or {@code null} if absent
     */
    V get(K key);

    /**
     * Inserts or updates the value for the given key.
     * If the cache is full, evicts the least recently used item first.
     *
     * @param key   the key
     * @param value the value to associate with the key
     */
    void put(K key, V value);

    /**
     * Returns the current number of entries in the cache.
     *
     * @return the size of the cache
     */
    int size();
}

