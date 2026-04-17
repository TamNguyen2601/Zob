package com.github.TamNguyen.Zob.service.job;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.domain.Permission;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResCreateJobDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResUpdateJobDTO;
import com.github.TamNguyen.Zob.repository.JobRepository;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.constant.PermissionCode;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;
import com.github.TamNguyen.Zob.util.error.PermissionException;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class JobApplicationService {

    private final JobRepository jobRepository;
    private final JobValidationService jobValidationService;
    private final JobStatusPolicy jobStatusPolicy;
    private final JobMappingService jobMapper;
    private final UserQueryService userQueryService;

    public JobApplicationService(JobRepository jobRepository,
            JobValidationService jobValidationService,
            JobStatusPolicy jobStatusPolicy,
            JobMappingService jobMapper,
            UserQueryService userQueryService) {
        this.jobRepository = jobRepository;
        this.jobValidationService = jobValidationService;
        this.jobStatusPolicy = jobStatusPolicy;
        this.jobMapper = jobMapper;
        this.userQueryService = userQueryService;
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
        this.enforceCreateOwnCompanyPermission(job);

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
        this.enforceScopedOwnCompanyPermission(
                jobInDb,
                PermissionCode.UPDATE_JOB_OWN_COMPANY,
                "cập nhật job",
                "Bạn chỉ được cập nhật job của công ty mình");

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

    public void delete(Job jobInDb) {
        this.enforceScopedOwnCompanyPermission(
                jobInDb,
                PermissionCode.DELETE_JOB_OWN_COMPANY,
                "xóa job",
                "Bạn chỉ được xóa job của công ty mình");
        this.jobRepository.deleteById(jobInDb.getId());
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

    private void enforceCreateOwnCompanyPermission(Job job) {
        this.enforceScopedOwnCompanyPermission(
                job,
                PermissionCode.CREATE_JOB_OWN_COMPANY,
                "tạo job",
                "Bạn chỉ được tạo job cho công ty của mình");
    }

    private void enforceScopedOwnCompanyPermission(
            Job job,
            String scopedPermissionName,
            String actionDisplayName,
            String companyMismatchMessage) {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new PermissionException("Bạn không có quyền " + actionDisplayName));

        User currentUser = this.userQueryService.findByEmail(email)
                .orElseThrow(() -> new PermissionException("Bạn không có quyền " + actionDisplayName));

        if (!hasPermissionByName(currentUser, scopedPermissionName)) {
            return;
        }

        if (currentUser.getCompany() == null || currentUser.getCompany().getId() == null) {
            throw new PermissionException(
                    "Tài khoản chưa được gán công ty nên không thể " + actionDisplayName);
        }

        if (job.getCompany() == null || job.getCompany().getId() == null) {
            throw new ValidationErrorException("Company là bắt buộc khi " + actionDisplayName);
        }

        if (!currentUser.getCompany().getId().equals(job.getCompany().getId())) {
            throw new PermissionException(companyMismatchMessage);
        }
    }

    private boolean hasPermissionByName(User user, String permissionName) {
        if (user.getRole() == null || user.getRole().getPermissions() == null) {
            return false;
        }

        return user.getRole().getPermissions().stream()
                .map(Permission::getName)
                .anyMatch(permissionName::equals);
    }
}