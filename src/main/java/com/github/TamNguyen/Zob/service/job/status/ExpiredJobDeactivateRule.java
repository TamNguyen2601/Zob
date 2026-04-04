package com.github.TamNguyen.Zob.service.job.status;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.Job;

@Component
public class ExpiredJobDeactivateRule implements JobStatusRule {

    @Override
    public boolean apply(Job job) {
        if (job == null || !job.isActive() || job.getEndDate() == null) {
            return false;
        }

        if (job.getEndDate().isBefore(Instant.now())) {
            job.setActive(false);
            return true;
        }

        return false;
    }
}