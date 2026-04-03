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

}
