package com.github.TamNguyen.Zob.service.job.validation;

import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;

@Component
public class DateRangeJobValidator implements JobValidator {

    @Override
    public boolean supports(JobValidationContext context) {
        return true;
    }

    @Override
    public void validate(Job inputJob, Job jobInDb) throws IdInvalidException {
        if (inputJob.getStartDate() != null
                && inputJob.getEndDate() != null
                && inputJob.getEndDate().isBefore(inputJob.getStartDate())) {
            throw new IdInvalidException("endDate must be after startDate");
        }
    }
}