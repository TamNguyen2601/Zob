package com.github.TamNguyen.Zob.service.premium;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.PremiumSubscription;
import com.github.TamNguyen.Zob.domain.Role;
import com.github.TamNguyen.Zob.domain.User;

@Service
public class PremiumAccessPolicy {

    public boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }
        Role role = user.getRole();
        if (role == null || role.getName() == null) {
            return false;
        }
        return "admin".equalsIgnoreCase(role.getName());
    }

    public boolean isPremium(User user, PremiumSubscription subscription, Instant now) {
        if (isAdmin(user)) {
            return true;
        }
        if (subscription == null || subscription.getEndAt() == null || now == null) {
            return false;
        }
        return subscription.getEndAt().isAfter(now);
    }
}
