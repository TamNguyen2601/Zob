package com.github.TamNguyen.Zob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.TamNguyen.Zob.domain.PaymentTransaction;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByProviderOrderId(String providerOrderId);

    boolean existsByProviderOrderId(String providerOrderId);
}
