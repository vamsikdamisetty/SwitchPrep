package com.cb.resilient.taskprocessor;

/**
 * Represents a unit of work to be processed by the task processing system.
 * Each task carries a unique identifier, a payload (input data), and tracks
 * how many processing attempts have been made (used for retry logic).
 */
class Task {

    /** Unique identifier for this task, used for idempotency checks and logging. */
    String taskId;

    /** Number of times this task has been attempted so far (starts at 0). */
    int attempt;

    /** The data or input associated with this task that will be processed. */
    String payload;

    /**
     * Creates a new Task with the given ID and payload.
     * The attempt counter is initialised to 0 (no processing attempts yet).
     *
     * @param taskId  a unique identifier for this task
     * @param payload the data to be processed
     */
    public Task(String taskId, String payload) {
        this.taskId = taskId;
        this.payload = payload;
        this.attempt = 0; // No attempts made yet
    }
}