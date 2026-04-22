package com.github.TamNguyen.Zob.service.payment.momo;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.TamNguyen.Zob.domain.PaymentTransaction;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.repository.PaymentTransactionRepository;
import com.github.TamNguyen.Zob.service.premium.PremiumSubscriptionService;
import com.github.TamNguyen.Zob.util.constant.PaymentTransactionStatusEnum;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class MomoIpnService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PremiumSubscriptionService premiumSubscriptionService;
    private final MomoPaymentClient momoPaymentClient;

    public MomoIpnService(
            PaymentTransactionRepository paymentTransactionRepository,
            PremiumSubscriptionService premiumSubscriptionService,
            MomoPaymentClient momoPaymentClient) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.premiumSubscriptionService = premiumSubscriptionService;
        this.momoPaymentClient = momoPaymentClient;
    }

    @Transactional
    public void handleIpn(
            String providerOrderId,
            long amount,
            int resultCode,
            String rawSignature,
            String signature) {

        if (!momoPaymentClient.verifyIpnSignature(rawSignature, signature)) {
            throw new ValidationErrorException("MoMo signature không hợp lệ");
        }

        PaymentTransaction tx = paymentTransactionRepository.findByProviderOrderId(providerOrderId)
                .orElseThrow(() -> new ValidationErrorException("Transaction không tồn tại"));

        if (tx.getStatus() == PaymentTransactionStatusEnum.SUCCESS
                || tx.getStatus() == PaymentTransactionStatusEnum.FAILED) {
            return;
        }

        if (tx.getAmount() != amount) {
            tx.setStatus(PaymentTransactionStatusEnum.FAILED);
            paymentTransactionRepository.save(tx);
            throw new ValidationErrorException("Amount không khớp với transaction");
        }

        if (resultCode == 0) {
            tx.setStatus(PaymentTransactionStatusEnum.SUCCESS);
            tx.setPaidAt(Instant.now());
            tx = paymentTransactionRepository.save(tx);

            User user = tx.getUser();
            premiumSubscriptionService.extendPremium(user, tx.getPlanCode(), tx.getPaidAt());
        } else {
            tx.setStatus(PaymentTransactionStatusEnum.FAILED);
            paymentTransactionRepository.save(tx);
        }
    }
}
