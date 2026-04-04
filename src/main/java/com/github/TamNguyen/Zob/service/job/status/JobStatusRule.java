package com.github.TamNguyen.Zob.service.job.status;

import com.github.TamNguyen.Zob.domain.Job;

public interface JobStatusRule {
    boolean apply(Job job);
}