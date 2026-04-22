package com.github.TamNguyen.Zob.service.payment;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.TamNguyen.Zob.domain.PaymentTransaction;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.repository.PaymentTransactionRepository;
import com.github.TamNguyen.Zob.service.premium.PremiumPlanCatalog;
import com.github.TamNguyen.Zob.util.constant.PaymentProviderEnum;
import com.github.TamNguyen.Zob.util.constant.PaymentTransactionStatusEnum;
import com.github.TamNguyen.Zob.util.constant.PremiumPlanCode;

@Service
public class PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PremiumPlanCatalog premiumPlanCatalog;

    public PaymentTransactionService(
            PaymentTransactionRepository paymentTransactionRepository,
            PremiumPlanCatalog premiumPlanCatalog) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.premiumPlanCatalog = premiumPlanCatalog;
    }

    @Transactional
    public PaymentTransaction createPendingMomoTransaction(User user, PremiumPlanCode planCode) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setUser(user);
        tx.setProvider(PaymentProviderEnum.MOMO);
        tx.setPlanCode(planCode);
        tx.setAmount(premiumPlanCatalog.getAmountVnd(planCode));
        tx.setProviderOrderId(generateProviderOrderId(user));
        tx.setStatus(PaymentTransactionStatusEnum.PENDING);
        tx.setCreatedAt(Instant.now());
        return paymentTransactionRepository.save(tx);
    }

    private String generateProviderOrderId(User user) {
        String random = UUID.randomUUID().toString().replace("-", "");
        long userId = user != null && user.getId() != null ? user.getId() : 0L;
        return "MOMO-" + userId + "-" + System.currentTimeMillis() + "-" + random;
    }
}
