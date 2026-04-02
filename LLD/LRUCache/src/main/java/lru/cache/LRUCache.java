package lru.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A generic, thread-safe Least Recently Used (LRU) Cache.
 * <p>
 * Backed by a {@link ConcurrentHashMap} for O(1) key lookups and a doubly-linked
 * list for O(1) eviction ordering. A {@link ReentrantLock} guards linked-list
 * mutations, keeping the critical section small while allowing high throughput
 * from hundreds of concurrent threads.
 * </p>
 *
 * <h3>Design Decisions</h3>
 * <ul>
 *   <li><b>ConcurrentHashMap</b> — provides lock-free reads and segmented writes,
 *       much better than a single {@code synchronized} block on the whole map.</li>
 *   <li><b>ReentrantLock on the doubly-linked list</b> — the list must maintain a
 *       strict total order (most-recent → least-recent). A single lock for
 *       list mutations is the simplest correct approach; the critical section is
 *       only a few pointer swaps so contention is minimal.</li>
 *   <li><b>Sentinel head/tail nodes</b> — eliminate null checks and edge-case
 *       branches in every list operation.</li>
 * </ul>
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class LRUCache<K, V> implements Cache<K, V> {

    // ──────────────────────────────────────────────────────────────────────
    //  Inner node for the doubly-linked list
    // ──────────────────────────────────────────────────────────────────────

    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Fields
    // ──────────────────────────────────────────────────────────────────────

    private final int capacity;
    private final ConcurrentHashMap<K, Node<K, V>> map;
    private final ReentrantLock lock = new ReentrantLock();

    /** Sentinel head — next points to the most recently used node. */
    private final Node<K, V> head;
    /** Sentinel tail — prev points to the least recently used node. */
    private final Node<K, V> tail;

    // ──────────────────────────────────────────────────────────────────────
    //  Constructor
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Creates a new LRU cache with the given maximum capacity.
     *
     * @param capacity the maximum number of entries the cache can hold
     * @throws IllegalArgumentException if capacity is &lt;= 0
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0, was: " + capacity);
        }
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>(capacity);

        // Wire up sentinel nodes
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Public API
    // ──────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * <p>
     * The map lookup is lock-free via {@link ConcurrentHashMap#get}.
     * If the key is found, the lock is acquired only to move the node
     * to the head of the access-order list.
     * </p>
     */
    @Override
    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null;
        }

        lock.lock();
        try {
            // Re-check: the node may have been evicted between the map.get
            // and acquiring the lock.
            if (map.get(key) != node) {
                return null;
            }
            moveToHead(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * The entire put is performed under the lock so that eviction + map
     * mutation + list mutation are atomic with respect to other writers.
     * </p>
     */
    @Override
    public void put(K key, V value) {
        lock.lock();
        try {
            Node<K, V> existing = map.get(key);
            if (existing != null) {
                // Update the value in place and promote to MRU
                existing.value = value;
                moveToHead(existing);
            } else {
                Node<K, V> newNode = new Node<>(key, value);
                map.put(key, newNode);
                addToHead(newNode);

                if (map.size() > capacity) {
                    Node<K, V> evicted = removeTail();
                    if (evicted != null) {
                        map.remove(evicted.key);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return map.size();
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Doubly-linked list helpers (must be called while holding the lock)
    // ──────────────────────────────────────────────────────────────────────

    /** Inserts {@code node} right after the sentinel head (MRU position). */
    private void addToHead(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    /** Unlinks {@code node} from its current position. */
    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /** Moves an existing node to the MRU position (head). */
    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    /**
     * Removes the node just before the sentinel tail (LRU position).
     *
     * @return the removed node, or {@code null} if the list is empty
     */
    private Node<K, V> removeTail() {
        Node<K, V> lru = tail.prev;
        if (lru == head) {
            return null; // list is empty (should never happen during eviction)
        }
        removeNode(lru);
        return lru;
    }
}

