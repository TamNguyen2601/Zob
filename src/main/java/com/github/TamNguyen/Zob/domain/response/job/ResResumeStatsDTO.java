package com.github.TamNguyen.Zob.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResResumeStatsDTO {
    private long total;
    private long pending;
    private long reviewing;
    private long approved;
    private long rejected;
}
