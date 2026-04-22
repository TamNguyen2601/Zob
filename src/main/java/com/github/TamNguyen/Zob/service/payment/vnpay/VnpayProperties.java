package com.github.TamNguyen.Zob.service.payment.vnpay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "vnpay")
public class VnpayProperties {
    /**
     * VNPay payment gateway base url (vd:
     * https://sandbox.vnpayment.vn/paymentv2/vpcpay.html)
     */
    private String payUrl;

    /** Website / APP return url sau khi người dùng thanh toán xong */
    private String returnUrl;

    /** Backend IPN url (vd: https://<ngrok>/api/v1/payments/vnpay/ipn) */
    private String ipnUrl;

    /** Terminal code (vnp_TmnCode) */
    private String tmnCode;

    /** Hash secret dùng ký/verify HMAC SHA512 */
    private String hashSecret;

    /** vnp_Version (mặc định 2.1.0) */
    private String version = "2.1.0";

    /** vnp_Command (mặc định pay) */
    private String command = "pay";

    /** vnp_CurrCode (mặc định VND) */
    private String currCode = "VND";

    /** vnp_Locale (mặc định vn) */
    private String locale = "vn";

    /** vnp_OrderType (optional, mặc định other) */
    private String orderType = "other";
}
