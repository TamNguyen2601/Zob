package com.github.TamNguyen.Zob.service.job.validation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.domain.Skill;
import com.github.TamNguyen.Zob.repository.SkillRepository;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;

@Component
public class SkillsJobValidator implements JobValidator {

    private final SkillRepository skillRepository;

    public SkillsJobValidator(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public boolean supports(JobValidationContext context) {
        return true;
    }

    @Override
    public void validate(Job inputJob, Job jobInDb) throws IdInvalidException {
        if (inputJob.getSkills() == null) {
            return;
        }

        List<Long> reqSkills = inputJob.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toList());

        List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
        if (dbSkills.size() != reqSkills.size()) {
            throw new IdInvalidException("Some skills are not found");
        }

        inputJob.setSkills(dbSkills);
        if (jobInDb != null) {
            jobInDb.setSkills(dbSkills);
        }
    }
}