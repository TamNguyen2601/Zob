package com.github.TamNguyen.Zob.service.job;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.domain.response.job.ResCreateJobDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResUpdateJobDTO;

public interface JobMappingService {
    ResCreateJobDTO toCreateDTO(Job job);

    ResUpdateJobDTO toUpdateDTO(Job job);
}