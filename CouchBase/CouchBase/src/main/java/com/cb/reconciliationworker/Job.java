package com.cb.reconciliationworker;

// Lifecycle states of a job (see README for details)
enum JobStatus {
    RUNNING, COMPLETED, FAILED
}

// Data model for a single unit of distributed work
class Job {

    String jobId;           // unique identifier
    JobStatus status;       // current lifecycle state
    long lastUpdatedTime;   // epoch-ms of last write; used for staleness detection

    public Job(String jobId, JobStatus status) {
        this.jobId = jobId;
        this.status = status;
        this.lastUpdatedTime = System.currentTimeMillis();
    }
}