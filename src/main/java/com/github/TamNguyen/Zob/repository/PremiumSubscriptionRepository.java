package com.github.TamNguyen.Zob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.TamNguyen.Zob.domain.PremiumSubscription;
import com.github.TamNguyen.Zob.domain.User;

@Repository
public interface PremiumSubscriptionRepository extends JpaRepository<PremiumSubscription, Long> {
    Optional<PremiumSubscription> findByUser(User user);
}
