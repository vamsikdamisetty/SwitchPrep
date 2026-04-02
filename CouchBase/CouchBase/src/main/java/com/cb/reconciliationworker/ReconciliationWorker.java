package com.cb.reconciliationworker;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/*
I designed a periodic reconciliation worker that scans for stale RUNNING jobs,
validates actual state via heartbeat or worker checks,
and applies idempotent fixes to ensure eventual consistency.
 */
// Reconciliation loop: detects zombie RUNNING jobs and marks them FAILED (see README)
class ReconciliationWorker implements Runnable {

    private final JobRepository repo;
    private final WorkerStateService workerService;
    private final long TIMEOUT = 5000; // ms before a RUNNING job is considered stale

    public ReconciliationWorker(JobRepository repo, WorkerStateService workerService) {
        this.repo = repo;
        this.workerService = workerService;
    }

    @Override
    public void run() {
        // 1. Find stale RUNNING jobs
        List<Job> staleJobs = repo.findRunningJobsOlderThan(TIMEOUT);

        for (Job job : staleJobs) {
            // 2. Confirm with the live-state service
            boolean actuallyRunning = workerService.isActuallyRunning(job.jobId);

            if (!actuallyRunning) {
                fix(job); // 3. Zombie confirmed → mark FAILED
            }else {
                System.out.println("Still running → skip; re-evaluate on next tick");
            }
            // Still running → skip; re-evaluate on next tick
        }
    }

    // Idempotent: marks the job FAILED; safe to call multiple times
    private void fix(Job job) {
        repo.updateStatus(job.jobId, JobStatus.FAILED);

        System.out.println("Reconciled job: " + job.jobId);
    }

}