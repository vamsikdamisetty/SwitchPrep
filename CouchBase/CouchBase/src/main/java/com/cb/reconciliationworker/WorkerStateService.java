package com.cb.reconciliationworker;

// Checks whether a job has a live worker owner (see README for production alternatives)
class WorkerStateService {

    // Demo: coin-flip (~50% dead, ~50% slow-but-alive); replace with real registry lookup
    public boolean isActuallyRunning(String jobId) {
        return Math.random() > 0.3;
    }
}