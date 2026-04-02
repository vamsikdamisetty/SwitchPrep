package com.cb.ratelimiter;

/**
 * Validates the RateLimiter across four scenarios:
 *  1. Burst limit  – rapid requests up to and beyond capacity
 *  2. Rejection    – requests fired while bucket is empty
 *  3. Refill       – bucket recovers after waiting
 *  4. Isolation    – separate users have independent buckets
 */
public class Client {

    // Bucket config mirrors RateLimiter: capacity=4, refill=5 tokens/sec
    private static final int    CAPACITY    = 4;
    private static final double REFILL_RATE = 5.0; // tokens per second

    public static void main(String[] args) throws InterruptedException {

        RateLimiter rateLimiter = new RateLimiter();

        // ── Scenario 1: Burst limit ────────────────────────────────────────────
        // Fire CAPACITY+2 rapid requests for user A.
        // First CAPACITY should be allowed; extras should be rejected.
        section("Scenario 1 – Burst limit (expect " + CAPACITY + " allowed, 2 rejected)");
        int allowed = 0, rejected = 0;
        for (int i = 1; i <= CAPACITY + 2; i++) {
            boolean result = rateLimiter.allow("userA");
            System.out.printf("  Request %d → %s%n", i, result ? "ALLOWED" : "REJECTED");
            if (result) allowed++; else rejected++;
        }
        System.out.printf("  Result: %d allowed, %d rejected  [expected: %d allowed, 2 rejected]%n",
                allowed, rejected, CAPACITY);

        // ── Scenario 2: Rejection while bucket is empty ────────────────────────
        // Immediately fire more requests — all should be rejected.
        section("Scenario 2 – Rejection while bucket is empty (all should be REJECTED)");
        for (int i = 1; i <= 3; i++) {
            boolean result = rateLimiter.allow("userA");
            System.out.printf("  Request %d → %s%n", i, result ? "ALLOWED" : "REJECTED");
        }

        // ── Scenario 3: Refill recovery ────────────────────────────────────────
        // Wait long enough for the bucket to refill (capacity / refillRate seconds),
        // then confirm requests are allowed again.
        long waitMs = (long) ((CAPACITY / REFILL_RATE) * 1000) + 200; // +200 ms buffer
        section("Scenario 3 – Refill recovery (waiting " + waitMs + " ms for bucket to refill)");
        Thread.sleep(waitMs);
        for (int i = 1; i <= CAPACITY; i++) {
            boolean result = rateLimiter.allow("userA");
            System.out.printf("  Request %d → %s%n", i, result ? "ALLOWED" : "REJECTED");
        }

        // ── Scenario 4: Per-user isolation ────────────────────────────────────
        // Exhaust userB's bucket; userC should be completely unaffected.
        section("Scenario 4 – Per-user isolation (userB exhausted, userC should be unaffected)");
        System.out.println("  Exhausting userB...");
        for (int i = 1; i <= CAPACITY + 1; i++) {
            System.out.printf("  userB request %d → %s%n", i,
                    rateLimiter.allow("userB") ? "ALLOWED" : "REJECTED");
        }
        System.out.println("  Sending fresh requests for userC...");
        for (int i = 1; i <= 3; i++) {
            System.out.printf("  userC request %d → %s%n", i,
                    rateLimiter.allow("userC") ? "ALLOWED" : "REJECTED");
        }
    }

    /** Prints a section header to separate test output. */
    private static void section(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
}
