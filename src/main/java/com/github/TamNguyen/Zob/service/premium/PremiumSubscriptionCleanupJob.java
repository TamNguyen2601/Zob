package com.github.TamNguyen.Zob.service.premium;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.TamNguyen.Zob.repository.PremiumSubscriptionRepository;

@Service
public class PremiumSubscriptionCleanupJob {

    private final PremiumSubscriptionRepository premiumSubscriptionRepository;

    public PremiumSubscriptionCleanupJob(PremiumSubscriptionRepository premiumSubscriptionRepository) {
        this.premiumSubscriptionRepository = premiumSubscriptionRepository;
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void cleanupExpiredSubscriptions() {
        Instant now = Instant.now();
        premiumSubscriptionRepository.deleteByEndAtBefore(now);
    }
}
