package com.github.TamNguyen.Zob.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.Job;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResPremiumMeDTO;
import com.github.TamNguyen.Zob.domain.response.job.ResResumeStatsDTO;
import com.github.TamNguyen.Zob.repository.JobRepository;
import com.github.TamNguyen.Zob.repository.ResumeRepository;
import com.github.TamNguyen.Zob.repository.UserRepository;
import com.github.TamNguyen.Zob.service.premium.PremiumQueryService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.constant.ResumeStateEnum;
import com.github.TamNguyen.Zob.util.error.NotFoundException;
import com.github.TamNguyen.Zob.util.error.PermissionException;

@Service
public class ResumeStatsService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final PremiumQueryService premiumQueryService;

    public ResumeStatsService(
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            UserRepository userRepository,
            PremiumQueryService premiumQueryService) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.premiumQueryService = premiumQueryService;
    }

    public ResResumeStatsDTO fetchResumeStatsByJobId(long jobId) {
        // 1. Get current user
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new PermissionException("Bạn chưa đăng nhập"));

        User currentUser = this.userRepository.findByEmail(email);
        if (currentUser == null) {
            throw new PermissionException("Người dùng không tồn tại");
        }

        // 2. Check Job exists
        Optional<Job> jobOptional = this.jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            throw new NotFoundException("Job not found");
        }

        // 3. Check Premium access
        ResPremiumMeDTO premiumStatus = this.premiumQueryService.getPremiumStatus(currentUser, Instant.now());
        if (!premiumStatus.isPremium()) {
            throw new PermissionException("Mua Prenium để xem thống kê hồ sơ nhé bạn yêu:3");
        }

        // 4. Fetch stats
        List<Object[]> counts = this.resumeRepository.countResumesByJobIdAndStatus(jobId);

        ResResumeStatsDTO stats = new ResResumeStatsDTO();
        long total = 0;

        for (Object[] row : counts) {
            ResumeStateEnum status = (ResumeStateEnum) row[0];
            long count = ((Number) row[1]).longValue();

            if (status == ResumeStateEnum.PENDING) {
                stats.setPending(count);
            } else if (status == ResumeStateEnum.REVIEWING) {
                stats.setReviewing(count);
            } else if (status == ResumeStateEnum.APPROVED) {
                stats.setApproved(count);
            } else if (status == ResumeStateEnum.REJECTED) {
                stats.setRejected(count);
            }
            total += count;
        }
        stats.setTotal(total);

        return stats;
    }
}
