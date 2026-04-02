package com.cb.resilient.taskprocessor;

/**
 * Simulates the actual processing of a {@link Task}.
 *
 * <p>This class intentionally introduces a random failure rate (30% chance)
 * to mimic real-world transient errors such as network timeouts, database
 * unavailability, or downstream service failures. The {@link ResilientTaskProcessor}
 * will catch these exceptions and apply retry-with-backoff logic.
 */
class TaskProcessor {

    /**
     * Attempts to process the given task.
     *
     * <p>There is a 30% probability that this method throws a
     * {@link RuntimeException} to simulate a transient failure. If processing
     * succeeds, the task ID is printed to standard output.
     *
     * @param task the task to process
     * @throws Exception if a simulated (or real) failure occurs during processing
     */
    public void process(Task task) throws Exception {
        // Simulate a transient failure: ~30% of tasks will fail on any given attempt.
        // This models real-world unreliable operations (e.g., network calls, I/O).
        if (Math.random() < 0.3) {
            throw new RuntimeException("Random failure");
        }

        // If no exception was thrown, the task has been processed successfully.
        System.out.println("Processed payload "+ task.payload + " for:" + task.taskId);
    }
}
