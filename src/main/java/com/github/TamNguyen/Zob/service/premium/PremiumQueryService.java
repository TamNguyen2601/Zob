package com.github.TamNguyen.Zob.service.premium;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.PremiumSubscription;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResPremiumMeDTO;
import com.github.TamNguyen.Zob.repository.PremiumSubscriptionRepository;

@Service
public class PremiumQueryService {

    private final PremiumSubscriptionRepository premiumSubscriptionRepository;
    private final PremiumAccessPolicy premiumAccessPolicy;

    public PremiumQueryService(
            PremiumSubscriptionRepository premiumSubscriptionRepository,
            PremiumAccessPolicy premiumAccessPolicy) {
        this.premiumSubscriptionRepository = premiumSubscriptionRepository;
        this.premiumAccessPolicy = premiumAccessPolicy;
    }

    public ResPremiumMeDTO getPremiumStatus(User user, Instant now) {
        if (premiumAccessPolicy.isAdmin(user)) {
            return new ResPremiumMeDTO(true, null, null);
        }

        PremiumSubscription subscription = premiumSubscriptionRepository.findByUser(user).orElse(null);
        boolean isPremium = premiumAccessPolicy.isPremium(user, subscription, now);
        if (!isPremium) {
            return new ResPremiumMeDTO(false, null, null);
        }
        return new ResPremiumMeDTO(true, subscription.getStartAt(), subscription.getEndAt());
    }
}
