package com.cb.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;

/*
I implemented a thread-safe token bucket rate limiter using per-key locking,
and for distributed systems I’d externalize state to Redis using atomic operations to ensure consistency across nodes.
 */
class RateLimiter {

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public boolean allow(String userId) {
        TokenBucket bucket = buckets.computeIfAbsent(
                userId,
                id -> new TokenBucket(4, 5) // capacity=10, refill=5/sec
        );

        return bucket.allowRequest();
    }
}
