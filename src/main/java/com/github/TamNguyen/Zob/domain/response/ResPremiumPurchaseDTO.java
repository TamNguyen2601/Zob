package com.github.TamNguyen.Zob.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResPremiumPurchaseDTO {
    private Long transactionId;
    private String providerOrderId;

    /**
     * payUrl: link web thanh toán (VNPay) — FE dùng để redirect hoặc tạo QR image
     * bằng thư viện qrcode.
     */
    private String payUrl;

    /**
     * Trường legacy để tương thích API cũ. Với VNPay thường không dùng.
     */
    private String momoDeeplink;

    private Instant createdAt;
}
