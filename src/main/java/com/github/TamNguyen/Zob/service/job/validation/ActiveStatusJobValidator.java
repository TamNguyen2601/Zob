package com.github.TamNguyen.Zob.service.job.validation;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;

@Component
public class ActiveStatusJobValidator implements JobValidator {

    @Override
    public boolean supports(JobValidationContext context) {
        return true;
    }

    @Override
    public void validate(Job inputJob, Job jobInDb) throws IdInvalidException {
        if (inputJob.isActive() && inputJob.getEndDate() != null && inputJob.getEndDate().isBefore(Instant.now())) {
            throw new IdInvalidException("Cannot set active=true for an already expired job");
        }
    }
}