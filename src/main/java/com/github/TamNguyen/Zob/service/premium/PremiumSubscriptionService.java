package com.github.TamNguyen.Zob.service.premium;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.TamNguyen.Zob.domain.PremiumSubscription;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.repository.PremiumSubscriptionRepository;
import com.github.TamNguyen.Zob.util.constant.PremiumPlanCode;

@Service
public class PremiumSubscriptionService {

    private final PremiumSubscriptionRepository premiumSubscriptionRepository;
    private final PremiumPlanCatalog premiumPlanCatalog;

    public PremiumSubscriptionService(
            PremiumSubscriptionRepository premiumSubscriptionRepository,
            PremiumPlanCatalog premiumPlanCatalog) {
        this.premiumSubscriptionRepository = premiumSubscriptionRepository;
        this.premiumPlanCatalog = premiumPlanCatalog;
    }

    @Transactional
    public PremiumSubscription extendPremium(User user, PremiumPlanCode planCode, Instant paidAt) {
        PremiumSubscription subscription = premiumSubscriptionRepository.findByUser(user)
                .orElseGet(() -> {
                    PremiumSubscription s = new PremiumSubscription();
                    s.setUser(user);
                    return s;
                });

        Instant now = paidAt;
        Instant currentEnd = subscription.getEndAt();
        Instant base = (currentEnd != null && currentEnd.isAfter(now)) ? currentEnd : now;
        Instant newEnd = premiumPlanCatalog.addDuration(base, planCode);

        if (subscription.getStartAt() == null) {
            subscription.setStartAt(now);
        }
        subscription.setEndAt(newEnd);

        return premiumSubscriptionRepository.save(subscription);
    }
}
