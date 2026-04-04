package com.github.TamNguyen.Zob.service.job;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.service.job.validation.JobValidationContext;
import com.github.TamNguyen.Zob.service.job.validation.JobValidator;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;

@Service
public class JobValidationService {

    private final List<JobValidator> validators;

    public JobValidationService(List<JobValidator> validators) {
        this.validators = validators;
    }

    public void validateForCreate(Job job) throws IdInvalidException {
        runValidators(JobValidationContext.CREATE, job, null);
    }

    public void validateForUpdate(Job inputJob, Job jobInDb) throws IdInvalidException {
        runValidators(JobValidationContext.UPDATE, inputJob, jobInDb);
    }

    private void runValidators(JobValidationContext context, Job inputJob, Job jobInDb) throws IdInvalidException {
        for (JobValidator validator : this.validators) {
            if (validator.supports(context)) {
                validator.validate(inputJob, jobInDb);
            }
        }
    }
}