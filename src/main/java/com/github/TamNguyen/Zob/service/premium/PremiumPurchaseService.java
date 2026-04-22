package com.github.TamNguyen.Zob.service.premium;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.TamNguyen.Zob.domain.PaymentTransaction;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResPremiumPurchaseDTO;
import com.github.TamNguyen.Zob.repository.PaymentTransactionRepository;
import com.github.TamNguyen.Zob.service.payment.PaymentTransactionService;
import com.github.TamNguyen.Zob.service.payment.momo.MomoPaymentClient;
import com.github.TamNguyen.Zob.util.constant.PremiumPlanCode;

@Service
public class PremiumPurchaseService {

    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final MomoPaymentClient momoPaymentClient;

    public PremiumPurchaseService(
            PaymentTransactionService paymentTransactionService,
            PaymentTransactionRepository paymentTransactionRepository,
            MomoPaymentClient momoPaymentClient) {
        this.paymentTransactionService = paymentTransactionService;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.momoPaymentClient = momoPaymentClient;
    }

    @Transactional
    public ResPremiumPurchaseDTO purchase(User user, PremiumPlanCode planCode) {
        PaymentTransaction tx = paymentTransactionService.createPendingMomoTransaction(user, planCode);

        String orderInfo = "Premium plan: " + planCode.name();
        MomoPaymentClient.CreatePaymentResult momoResult =
                momoPaymentClient.createPayment(tx.getProviderOrderId(), tx.getAmount(), orderInfo);

        // Lưu payUrl vào DB (FE dùng để tạo QR image bằng thư viện)
        tx.setQrCodeUrl(momoResult.payUrl());
        tx = paymentTransactionRepository.save(tx);

        return new ResPremiumPurchaseDTO(
                tx.getId(),
                tx.getProviderOrderId(),
                momoResult.payUrl(),      // FE dùng cái này để tạo QR
                momoResult.momoDeeplink(), // FE có thể dùng cái này cho nút "Mở app MoMo"
                tx.getCreatedAt());
    }
}
