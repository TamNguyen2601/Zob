package com.github.TamNguyen.Zob.service.job.validation;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.Company;
import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.repository.CompanyRepository;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;

@Component
public class CompanyJobValidator implements JobValidator {

    private final CompanyRepository companyRepository;

    public CompanyJobValidator(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public boolean supports(JobValidationContext context) {
        return true;
    }

    @Override
    public void validate(Job inputJob, Job jobInDb) throws IdInvalidException {
        if (inputJob.getCompany() == null) {
            return;
        }

        Optional<Company> companyOptional = this.companyRepository.findById(inputJob.getCompany().getId());
        if (!companyOptional.isPresent()) {
            throw new IdInvalidException("Company not found");
        }

        Company company = companyOptional.get();
        inputJob.setCompany(company);
        if (jobInDb != null) {
            jobInDb.setCompany(company);
        }
    }
}