package com.cb.ratelimiter;

/**
 * Token Bucket rate limiter.
 * Allows bursts up to {@code capacity} requests, then enforces {@code refillRate} requests/sec.
 */
class TokenBucket {

    /** Max tokens the bucket can hold; controls burst size. */
    private final int capacity;

    /** Tokens added per second. */
    private final double refillRate;

    /** Currently available tokens. */
    private double tokens;

    /** When the bucket was last refilled (milliseconds). */
    private long lastRefillTime;

    /** Starts the bucket full. */
    public TokenBucket(int capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

    /**
     * Returns {@code true} if the request is allowed (a token was consumed),
     * or {@code false} if the bucket is empty and the request is rejected.
     */
    public synchronized boolean allowRequest() {
        refill(); // top up before checking

        if (tokens >= 1) {
            tokens -= 1; // consume one token
            return true;
        }
        return false; // rate limit exceeded
    }

    /** Adds tokens proportional to elapsed time, capped at capacity. */
    private void refill() {
        long now = System.currentTimeMillis();

        // elapsed seconds × rate = tokens earned since last refill
        double tokensToAdd = (now - lastRefillTime) / 1000.0 * refillRate;

        tokens = Math.min(capacity, tokens + tokensToAdd); // never exceed capacity
        lastRefillTime = now;
    }
}