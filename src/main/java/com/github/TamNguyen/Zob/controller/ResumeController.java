package com.github.TamNguyen.Zob.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.TamNguyen.Zob.domain.Company;
import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.domain.Permission;
import com.github.TamNguyen.Zob.domain.Resume;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.domain.response.resume.ResCreateResumeDTO;
import com.github.TamNguyen.Zob.domain.response.resume.ResFetchResumeDTO;
import com.github.TamNguyen.Zob.domain.response.resume.ResUpdateResumeDTO;
import com.github.TamNguyen.Zob.service.ResumeService;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.annotation.ApiMessage;
import com.github.TamNguyen.Zob.util.constant.PermissionCode;
import com.github.TamNguyen.Zob.util.error.NotFoundException;
import com.github.TamNguyen.Zob.util.error.PermissionException;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserQueryService userQueryService;

    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(
            ResumeService resumeService,
            UserQueryService userQueryService,
            FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userQueryService = userQueryService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) {
        // check id exists
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new ValidationErrorException("User id/Job id không tồn tại");
        }

        // create new resume
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) {
        // check id exist
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(resume.getId());
        if (reqResumeOptional.isEmpty()) {
            throw new NotFoundException("Resume với id = " + resume.getId() + " không tồn tại");
        }

        Resume reqResume = reqResumeOptional.get();
        reqResume.setStatus(resume.getStatus());

        return ResponseEntity.ok().body(this.resumeService.update(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new NotFoundException("Resume với id = " + id + " không tồn tại");
        }

        Resume resumeInDb = reqResumeOptional.get();
        this.validateResumeDeletePermission(resumeInDb);

        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    private void validateResumeDeletePermission(Resume resumeInDb) {
        String currentUserEmail = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new PermissionException("Bạn không có quyền xóa CV này"));

        User currentUser = this.userQueryService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new PermissionException("Bạn không có quyền xóa CV này"));

        if (hasDeleteResumePermission(currentUser)) {
            return;
        }

        if (isOwnerOfResume(currentUserEmail, resumeInDb)) {
            return;
        }

        throw new PermissionException("Bạn chỉ được xóa CV do chính mình tạo");
    }

    private boolean hasDeleteResumePermission(User user) {
        if (user.getRole() == null || user.getRole().getPermissions() == null) {
            return false;
        }

        return user.getRole().getPermissions().stream()
                .map(Permission::getName)
                .anyMatch(PermissionCode.DELETE_RESUME::equals);
    }

    private boolean isOwnerOfResume(String currentUserEmail, Resume resumeInDb) {
        if (resumeInDb.getUser() != null && resumeInDb.getUser().getEmail() != null) {
            return currentUserEmail.equalsIgnoreCase(resumeInDb.getUser().getEmail());
        }

        return resumeInDb.getEmail() != null && currentUserEmail.equalsIgnoreCase(resumeInDb.getEmail());
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch a resume by id")
    public ResponseEntity<ResFetchResumeDTO> fetchById(@PathVariable("id") long id) {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new NotFoundException("Resume với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(this.resumeService.getResume(reqResumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {

        Specification<Resume> jobInSpec = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        Optional<User> currentUserOptional = this.userQueryService.findByEmail(email);
        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job.company.id")
                        .equal(filterBuilder.input(userCompany.getId())).get());
            }
        }

        Specification<Resume> finalSpec = jobInSpec == null ? spec : jobInSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
