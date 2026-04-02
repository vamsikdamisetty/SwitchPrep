package com.cb.resilient.taskprocessor;

/**
 * Entry point for the resilient task processing demo.
 *
 * <p>This class sets up the system, starts the dispatcher loop on a background
 * thread, submits 10 unique tasks, and then intentionally submits a duplicate
 * task to demonstrate the idempotency guard built into {@link ResilientTaskProcessor}.
 */
public class Main {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        // Create a resilient processor backed by a pool of 5 worker threads
        ResilientTaskProcessor system = new ResilientTaskProcessor(5);

        Runnable r = () -> {
            system.start();
        };
        // Start the dispatcher loop on a dedicated background thread.
        // The loop blocks on the queue and offloads each task to the thread pool.
        new Thread(r).start();

        // Submit 10 unique tasks (task-0 through task-9) to the processing queue
        for (int i = 0; i < 10; i++) {
            system.submit(new Task("task-" + i, "data_" +(i+1000)));
        }

        // Duplicate test: submit task-1 again to verify idempotency —
        // the processor should detect it as already processed and skip it.
        system.submit(new Task("task-1", "duplicate"));
    }
}
