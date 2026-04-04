package com.github.TamNguyen.Zob.service.job;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.service.job.status.JobStatusRule;

@Component
public class JobStatusPolicy {

    private final List<JobStatusRule> rules;

    public JobStatusPolicy(List<JobStatusRule> rules) {
        this.rules = rules;
    }

    public boolean apply(Job job) {
        if (job == null) {
            return false;
        }

        boolean changed = false;
        for (JobStatusRule rule : this.rules) {
            changed = rule.apply(job) || changed;
        }
        return changed;
    }
}