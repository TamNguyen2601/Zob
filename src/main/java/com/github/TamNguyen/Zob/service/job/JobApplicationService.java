package com.github.TamNguyen.Zob.service.job;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResCreateJobDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResUpdateJobDTO;
import com.github.TamNguyen.Zob.repository.JobRepository;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class JobApplicationService {

    private final JobRepository jobRepository;
    private final JobValidationService jobValidationService;
    private final JobStatusPolicy jobStatusPolicy;
    private final JobMappingService jobMapper;

    public JobApplicationService(JobRepository jobRepository,
            JobValidationService jobValidationService,
            JobStatusPolicy jobStatusPolicy,
            JobMappingService jobMapper) {
        this.jobRepository = jobRepository;
        this.jobValidationService = jobValidationService;
        this.jobStatusPolicy = jobStatusPolicy;
        this.jobMapper = jobMapper;
    }

    public Optional<Job> fetchJobById(long id) {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        if (!jobOptional.isPresent()) {
            return jobOptional;
        }

        Job currentJob = jobOptional.get();
        if (this.jobStatusPolicy.apply(currentJob)) {
            currentJob = this.jobRepository.save(currentJob);
        }

        return Optional.of(currentJob);
    }

    public ResCreateJobDTO create(Job job) {
        try {
            this.jobValidationService.validateForCreate(job);
        } catch (IdInvalidException e) {
            throw new ValidationErrorException(e.getMessage());
        }

        Job currentJob = this.jobRepository.save(job);
        if (this.jobStatusPolicy.apply(currentJob)) {
            currentJob = this.jobRepository.save(currentJob);
        }

        return this.jobMapper.toCreateDTO(currentJob);
    }

    public ResUpdateJobDTO update(Job inputJob, Job jobInDb) {
        try {
            this.jobValidationService.validateForUpdate(inputJob, jobInDb);
        } catch (IdInvalidException e) {
            throw new ValidationErrorException(e.getMessage());
        }

        jobInDb.setName(inputJob.getName());
        jobInDb.setSalary(inputJob.getSalary());
        jobInDb.setQuantity(inputJob.getQuantity());
        jobInDb.setLocation(inputJob.getLocation());
        jobInDb.setLevel(inputJob.getLevel());
        jobInDb.setStartDate(inputJob.getStartDate());
        jobInDb.setEndDate(inputJob.getEndDate());
        jobInDb.setActive(inputJob.isActive());

        Job currentJob = this.jobRepository.save(jobInDb);
        if (this.jobStatusPolicy.apply(currentJob)) {
            currentJob = this.jobRepository.save(currentJob);
        }

        return this.jobMapper.toUpdateDTO(currentJob);
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        pageJob.getContent().forEach(job -> {
            if (this.jobStatusPolicy.apply(job)) {
                this.jobRepository.save(job);
            }
        });

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageJob.getContent());
        return rs;
    }
}