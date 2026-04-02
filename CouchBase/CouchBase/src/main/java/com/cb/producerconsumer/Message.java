package com.cb.producerconsumer;

/**
 * Message represents a unit of work/data that gets produced and consumed.
 *
 * In a real system this could be:
 *   - An order event from an e-commerce platform
 *   - A log entry to be processed
 *   - A notification payload
 *
 * Keeping it immutable (final fields) ensures thread-safety:
 * once constructed, no thread can accidentally mutate it.
 */
public final class Message {

    /** Unique identifier for this message (useful for tracing). */
    private final int id;

    /** The actual payload — in production this might be JSON, Avro, Protobuf, etc. */
    private final String payload;

    /** Timestamp (ms since epoch) when the message was created by the producer. */
    private final long createdAt;

    public Message(int id, String payload) {
        this.id        = id;
        this.payload   = payload;
        this.createdAt = System.currentTimeMillis();
    }

    public int    getId()        { return id; }
    public String getPayload()   { return payload; }
    public long   getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("Message{id=%d, payload='%s', createdAt=%d}", id, payload, createdAt);
    }
}

