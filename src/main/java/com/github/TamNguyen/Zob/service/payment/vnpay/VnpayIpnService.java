package com.github.TamNguyen.Zob.service.payment.vnpay;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.TamNguyen.Zob.domain.PaymentTransaction;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.repository.PaymentTransactionRepository;
import com.github.TamNguyen.Zob.service.premium.PremiumSubscriptionService;
import com.github.TamNguyen.Zob.util.constant.PaymentTransactionStatusEnum;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class VnpayIpnService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PremiumSubscriptionService premiumSubscriptionService;
    private final VnpayPaymentClient vnpayPaymentClient;

    public VnpayIpnService(
            PaymentTransactionRepository paymentTransactionRepository,
            PremiumSubscriptionService premiumSubscriptionService,
            VnpayPaymentClient vnpayPaymentClient) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.premiumSubscriptionService = premiumSubscriptionService;
        this.vnpayPaymentClient = vnpayPaymentClient;
    }

    @Transactional
    public void handleIpn(Map<String, String> params) {
        if (!vnpayPaymentClient.verifySignature(params)) {
            throw new ValidationErrorException("VNPay signature không hợp lệ");
        }

        String txnRef = params.get("vnp_TxnRef");
        if (!StringUtils.hasText(txnRef)) {
            throw new ValidationErrorException("Thiếu vnp_TxnRef");
        }

        long amount = parseLong(params.get("vnp_Amount"));
        long amountVnd = amount / 100;

        String responseCode = params.getOrDefault("vnp_ResponseCode", "");
        String transactionStatus = params.getOrDefault("vnp_TransactionStatus", "");

        PaymentTransaction tx = paymentTransactionRepository.findByProviderOrderId(txnRef)
                .orElseThrow(() -> new ValidationErrorException("Transaction không tồn tại"));

        if (tx.getStatus() == PaymentTransactionStatusEnum.SUCCESS
                || tx.getStatus() == PaymentTransactionStatusEnum.FAILED) {
            return;
        }

        if (tx.getAmount() != amountVnd) {
            tx.setStatus(PaymentTransactionStatusEnum.FAILED);
            paymentTransactionRepository.save(tx);
            throw new ValidationErrorException("Amount không khớp với transaction");
        }

        boolean isSuccess = "00".equals(responseCode)
                && (transactionStatus.isEmpty() || "00".equals(transactionStatus));
        if (isSuccess) {
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

    private long parseLong(String value) {
        try {
            if (!StringUtils.hasText(value)) {
                return 0L;
            }
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0L;
        }
    }
}
