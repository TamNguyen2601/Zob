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
     * payUrl: link web thanh toán MoMo — FE dùng để tạo QR image bằng thư viện qrcode.
     * Bất kỳ QR scanner nào cũng đọc được, mở trang web thanh toán MoMo.
     */
    private String payUrl;

    /**
     * momoDeeplink: deep link momo://... — chỉ app MoMo mới hiểu.
     * FE có thể dùng làm nút "Mở app MoMo" trên mobile.
     */
    private String momoDeeplink;

    private Instant createdAt;
}
