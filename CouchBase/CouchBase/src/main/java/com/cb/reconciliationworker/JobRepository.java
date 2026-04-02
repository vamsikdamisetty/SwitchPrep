package com.cb.reconciliationworker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// Thread-safe in-memory job store (replace with CouchBase/Redis in production)
class JobRepository {

    private final Map<String, Job> db = new ConcurrentHashMap<>(); // jobId → Job

    // Insert or overwrite a job
    public void save(Job job) {
        db.put(job.jobId, job);
    }

    // Return RUNNING jobs whose lastUpdatedTime exceeds thresholdMillis (stale / zombie candidates)
    public List<Job> findRunningJobsOlderThan(long thresholdMillis) {
        long now = System.currentTimeMillis();
//        List<Job> result = new ArrayList<>();
//        for (Job job : db.values()) {
//            if (job.status == JobStatus.RUNNING &&
//                    now - job.lastUpdatedTime > thresholdMillis) {
//                result.add(job);
//            }
//        }

        List<Job> jobList = db.values().stream()
                .filter(job -> job.status == JobStatus.RUNNING && now - job.lastUpdatedTime > thresholdMillis)
                .collect(Collectors.toList());
        return jobList;
    }

    // Idempotently update a job's status and refresh its timestamp
    public void updateStatus(String jobId, JobStatus status) {
        Job job = db.get(jobId);
        if (job != null) {
            System.out.println(jobId + " :: New Status : " + status.toString()  );
            job.status = status;
            job.lastUpdatedTime = System.currentTimeMillis();
        }
    }
}