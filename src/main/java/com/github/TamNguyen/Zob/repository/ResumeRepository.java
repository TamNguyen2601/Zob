package com.github.TamNguyen.Zob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>,
                JpaSpecificationExecutor<Resume> {

    java.util.List<Resume> findByUser(User user);

    @org.springframework.data.jpa.repository.Query("SELECT r.status, COUNT(r.id) FROM Resume r WHERE r.job.id = :jobId GROUP BY r.status")
    java.util.List<Object[]> countResumesByJobIdAndStatus(@org.springframework.data.repository.query.Param("jobId") long jobId);

}
