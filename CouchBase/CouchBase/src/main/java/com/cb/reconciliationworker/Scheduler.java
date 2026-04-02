package com.cb.reconciliationworker;

import java.util.concurrent.*;

/**
 * Entry point: wires dependencies and runs the reconciler every 10 s (see README)
 */
class Scheduler {

    public static void main(String[] args) throws InterruptedException {

        JobRepository repo = new JobRepository();
        WorkerStateService workerService = new WorkerStateService();

        // Seed demo jobs — no heartbeat updates, so they turn stale after 5 s
        repo.save(new Job("job1", JobStatus.RUNNING));
        repo.save(new Job("job2", JobStatus.RUNNING));

        ReconciliationWorker worker = new ReconciliationWorker(repo, workerService);

        // Single thread: prevents concurrent sweeps from double-fixing jobs
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Start immediately, repeat every 10 s (scheduleAtFixedRate = fixed clock ticks)
        scheduler.scheduleAtFixedRate(worker, 0, 5, TimeUnit.SECONDS);

        Thread.sleep(30000);

        scheduler.shutdown();
    }
}
