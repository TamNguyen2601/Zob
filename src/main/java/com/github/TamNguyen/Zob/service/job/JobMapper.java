package com.github.TamNguyen.Zob.service.job;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.domain.response.job.ResCreateJobDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResUpdateJobDTO;

@Component
public class JobMapper implements JobMappingService {

    @Override
    public ResCreateJobDTO toCreateDTO(Job job) {
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLocation(job.getLocation());
        dto.setLevel(job.getLevel());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setActive(job.isActive());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setCreatedBy(job.getCreatedBy());
        dto.setSkills(getSkillNames(job));
        return dto;
    }

    @Override
    public ResUpdateJobDTO toUpdateDTO(Job job) {
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLocation(job.getLocation());
        dto.setLevel(job.getLevel());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setActive(job.isActive());
        dto.setUpdatedAt(job.getUpdatedAt());
        dto.setUpdatedBy(job.getUpdatedBy());
        dto.setSkills(getSkillNames(job));
        return dto;
    }

    private List<String> getSkillNames(Job job) {
        if (job.getSkills() == null) {
            return null;
        }

        return job.getSkills().stream()
                .map(item -> item.getName())
                .collect(Collectors.toList());
    }
}