package com.cb.resilient.taskprocessor;

import java.util.concurrent.*;

/**
 * A resilient, multi-threaded task processing system with the following capabilities:
 *
 * <ul>
 *   <li><b>Concurrency</b> – a fixed thread pool processes tasks in parallel.</li>
 *   <li><b>Idempotency</b> – duplicate tasks (same {@code taskId}) are detected and
 *       skipped so each task is processed <em>at most once</em>.</li>
 *   <li><b>Retry with exponential back-off</b> – if a task fails, it is re-queued
 *       up to {@code MAX_RETRIES} times, waiting {@code attempt × 1 second} between
 *       each attempt to give transient errors time to resolve.</li>
 * </ul>
 */
class ResilientTaskProcessor {

    /**
     * Unbounded blocking queue that holds tasks waiting to be processed.
     * Producers (callers of {@link #submit}) add tasks here; the dispatcher
     * loop in {@link #start} drains them.
     */
    private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

    /**
     * Fixed-size thread pool that executes tasks concurrently.
     * The pool size is determined at construction time via the {@code workers} parameter.
     */
    private final ExecutorService executor;

    /** Delegate that contains the actual processing logic for a single task. */
    private final TaskProcessor processor = new TaskProcessor();

    /**
     * Thread-safe set of task IDs that have already been successfully processed.
     * Used to enforce idempotency: if a task ID is already present, the task is skipped.
     * The Boolean value is a placeholder; only key presence matters.
     */
    private final ConcurrentHashMap<String, Boolean> processed = new ConcurrentHashMap<>();

    /**
     * Maximum number of retry attempts before a task is considered permanently failed.
     * On each retry the task's {@code attempt} counter is incremented and used to
     * calculate the back-off delay.
     */
    private final int MAX_RETRIES = 3;

    /**
     * Constructs a new {@code ResilientTaskProcessor} with the specified worker count.
     *
     * @param workers number of threads in the underlying fixed thread pool
     */
    public ResilientTaskProcessor(int workers) {
        this.executor = Executors.newFixedThreadPool(workers);
    }

    /**
     * Submits a task to the processing queue.
     *
     * <p>This method is non-blocking: it inserts the task at the tail of the queue
     * and returns immediately. The task will be picked up by the dispatcher loop
     * running in a separate thread.
     *
     * @param task the task to enqueue
     */
    public void submit(Task task) {
        queue.offer(task);
    }

    /**
     * Starts the dispatcher loop that continuously drains the task queue.
     *
     * <p>Each task dequeued is handed off to the thread pool via
     * {@link ExecutorService#submit}, so multiple tasks are processed concurrently.
     * The loop runs indefinitely; the calling thread should therefore be a dedicated
     * background thread (see {@link Main#main}).
     *
     * <p>If the thread is interrupted while waiting on the queue, the interrupt
     * flag is restored and the loop exits gracefully.
     */
    public void start() {
        while (true) {
            try {
                // Block until a task becomes available in the queue
                Task task = queue.take();

                // Offload task handling to a worker thread so the dispatcher
                // can immediately pick up the next queued task
                executor.submit(() -> handleTask(task));

            } catch (InterruptedException e) {
                // Restore the interrupted status so callers can detect the interruption,
                // then exit the loop to allow clean shutdown
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Core task handling logic executed by a worker thread.
     *
     * <ol>
     *   <li>Checks whether the task has already been processed (idempotency guard).</li>
     *   <li>Delegates to {@link TaskProcessor#process(Task)} to do the real work.</li>
     *   <li>On success, records the task ID in {@code processed} to prevent re-processing.</li>
     *   <li>On failure, retries the task (with back-off) up to {@code MAX_RETRIES} times;
     *       after that, logs a permanent failure.</li>
     * </ol>
     *
     * @param task the task to handle
     */
    private void handleTask(Task task) {

        // ── Idempotency Check ──────────────────────────────────────────────────────
        // If this task ID was already processed successfully, skip it.
        // This guards against duplicate submissions (e.g. the duplicate test in Main).
        if (processed.containsKey(task.taskId)) {
            System.out.println("Skipping duplicate: " + task.taskId);
            return;
        }

        try {
            // Delegate to the processor; may throw an exception on simulated failure
            processor.process(task);

            // Mark the task as successfully processed so future duplicates are skipped
            processed.put(task.taskId, true);

        } catch (Exception e) {
            // ── Retry Logic ───────────────────────────────────────────────────────
            if (task.attempt < MAX_RETRIES) {
                // Increment the attempt counter before re-queuing
                task.attempt++;

                System.out.println("Retrying: " + task.taskId +
                        " attempt=" + task.attempt);

                // Re-queue after an exponential back-off delay
                retry(task);
            } else {
                // All retry attempts exhausted — log the permanent failure
                System.out.println("Failed permanently: " + task.taskId);
            }
        }
    }

    /**
     * Re-queues a failed task after a back-off delay.
     *
     * <p>The delay grows linearly with the attempt number:
     * {@code delay = attempt × 1000 ms}. This gives the underlying system
     * (or simulated failure) time to recover before the next attempt.
     *
     * @param task the task to retry; its {@code attempt} field must already be incremented
     */
    private void retry(Task task) {
        try {
            // Back-off delay: wait (attempt × 1s) before re-submitting.
            // E.g. attempt 1 → 1 s, attempt 2 → 2 s, attempt 3 → 3 s.
            Thread.sleep(1000L * task.attempt);

            // Re-add the task to the tail of the queue for another processing attempt
            queue.offer(task);
        } catch (InterruptedException e) {
            // Restore interrupted status if the sleep is interrupted during shutdown
            Thread.currentThread().interrupt();
        }
    }
}
