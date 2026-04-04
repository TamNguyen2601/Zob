package com.github.TamNguyen.Zob.service.job.validation;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;

public interface JobValidator {
    boolean supports(JobValidationContext context);

    void validate(Job inputJob, Job jobInDb) throws IdInvalidException;
}