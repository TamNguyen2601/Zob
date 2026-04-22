package com.github.TamNguyen.Zob.service.premium;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.TamNguyen.Zob.domain.PaymentTransaction;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResPremiumPurchaseDTO;
import com.github.TamNguyen.Zob.repository.PaymentTransactionRepository;
import com.github.TamNguyen.Zob.service.payment.PaymentTransactionService;
import com.github.TamNguyen.Zob.service.payment.vnpay.VnpayPaymentClient;
import com.github.TamNguyen.Zob.util.constant.PremiumPlanCode;

@Service
public class PremiumPurchaseService {

    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final VnpayPaymentClient vnpayPaymentClient;

    public PremiumPurchaseService(
            PaymentTransactionService paymentTransactionService,
            PaymentTransactionRepository paymentTransactionRepository,
            VnpayPaymentClient vnpayPaymentClient) {
        this.paymentTransactionService = paymentTransactionService;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.vnpayPaymentClient = vnpayPaymentClient;
    }

    @Transactional
    public ResPremiumPurchaseDTO purchase(User user, PremiumPlanCode planCode, String clientIp) {
        PaymentTransaction tx = paymentTransactionService.createPendingVnpayTransaction(user, planCode);

        String orderInfo = "Premium plan: " + planCode.name();
        String paymentUrl = vnpayPaymentClient.createPaymentUrl(
                tx.getProviderOrderId(),
                tx.getAmount(),
                orderInfo,
                clientIp);

        // Lưu paymentUrl vào DB (FE dùng để tạo QR image bằng thư viện qrcode)
        tx.setQrCodeUrl(paymentUrl);
        tx = paymentTransactionRepository.save(tx);

        return new ResPremiumPurchaseDTO(
                tx.getId(),
                tx.getProviderOrderId(),
                paymentUrl, // FE dùng cái này để tạo QR/redirect
                null,
                tx.getCreatedAt());
    }
}
