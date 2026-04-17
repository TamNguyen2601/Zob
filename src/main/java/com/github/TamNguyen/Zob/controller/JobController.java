package com.github.TamNguyen.Zob.controller;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResCreateJobDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResUpdateJobDTO;
import com.github.TamNguyen.Zob.service.job.JobApplicationService;
import com.github.TamNguyen.Zob.util.annotation.ApiMessage;
import com.github.TamNguyen.Zob.util.error.NotFoundException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobApplicationService jobApplicationService;

    public JobController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobApplicationService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) {
        Optional<Job> currentJob = this.jobApplicationService.fetchJobById(job.getId());
        if (!currentJob.isPresent()) {
            throw new NotFoundException("Job not found");
        }

        return ResponseEntity.ok()
                .body(this.jobApplicationService.update(job, currentJob.get()));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        Optional<Job> currentJob = this.jobApplicationService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new NotFoundException("Job not found");
        }
        this.jobApplicationService.delete(currentJob.get());
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) {
        Optional<Job> currentJob = this.jobApplicationService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new NotFoundException("Job not found");
        }

        return ResponseEntity.ok().body(currentJob.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("Get job with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable) {

        return ResponseEntity.ok().body(this.jobApplicationService.fetchAll(spec, pageable));
    }
}
