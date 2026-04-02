package com.github.TamNguyen.Zob.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.github.TamNguyen.Zob.domain.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>,
                JpaSpecificationExecutor<Skill> {

        boolean existsByName(String name);

        List<Skill> findByIdIn(List<Long> id);
}
