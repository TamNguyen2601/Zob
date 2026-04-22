package com.github.TamNguyen.Zob.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.TamNguyen.Zob.service.payment.vnpay.VnpayIpnService;
import com.github.TamNguyen.Zob.util.annotation.ApiMessage;

@RequestMapping("/api/v1")
@RestController
public class VnpayPaymentController {

    private static final Logger log = LoggerFactory.getLogger(VnpayPaymentController.class);

    /** URL trang kết quả trên FE */
    private static final String FE_RESULT_URL = "http://localhost:4173/payment/result";

    private final VnpayIpnService vnpayIpnService;

    public VnpayPaymentController(VnpayIpnService vnpayIpnService) {
        this.vnpayIpnService = vnpayIpnService;
    }

    // ─── IPN: VNPay gọi server-to-server (khi portal đã config IPN URL) ───────
    @GetMapping("/payments/vnpay/ipn")
    @ApiMessage("VNPay IPN callback")
    public ResponseEntity<Map<String, String>> ipn(@RequestParam Map<String, String> params) {
        log.info("[VNPay IPN] ===== Nhận callback từ VNPay =====");
        log.info("[VNPay IPN] ResponseCode={} | TxnRef={} | Amount={} | TransactionStatus={}",
                params.get("vnp_ResponseCode"),
                params.get("vnp_TxnRef"),
                params.get("vnp_Amount"),
                params.get("vnp_TransactionStatus"));

        Map<String, String> res = new HashMap<>();
        try {
            vnpayIpnService.handleIpn(params);
            log.info("[VNPay IPN] ✅ Xử lý thành công - TxnRef={}", params.get("vnp_TxnRef"));
            res.put("RspCode", "00");
            res.put("Message", "Confirm Success");
        } catch (Exception ex) {
            log.error("[VNPay IPN] ❌ Xử lý thất bại | TxnRef={} | Lỗi: {}",
                    params.get("vnp_TxnRef"), ex.getMessage(), ex);
            res.put("RspCode", "97");
            res.put("Message", "Invalid Signature");
        }
        return ResponseEntity.ok(res);
    }

    // ─── Return URL: VNPay redirect browser về đây sau khi user thanh toán ─────
    // Đây là phương án thay thế IPN khi không cấu hình được IPN URL trong portal.
    @GetMapping("/payments/vnpay/return")
    public ResponseEntity<Void> returnUrl(@RequestParam Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.getOrDefault("vnp_ResponseCode", "99");

        log.info("[VNPay Return] ===== Nhận redirect từ VNPay =====");
        log.info("[VNPay Return] ResponseCode={} | TxnRef={} | Amount={}",
                responseCode, txnRef, params.get("vnp_Amount"));

        String redirectTarget;
        try {
            // Tái sử dụng handleIpn — cùng logic verify + nâng Premium
            vnpayIpnService.handleIpn(params);
            log.info("[VNPay Return] ✅ Nâng Premium thành công - TxnRef={}", txnRef);
            redirectTarget = FE_RESULT_URL + "?success=true&code=" + responseCode + "&txnRef=" + encode(txnRef);
        } catch (Exception ex) {
            log.error("[VNPay Return] ❌ Xử lý thất bại | TxnRef={} | Lỗi: {}", txnRef, ex.getMessage(), ex);
            redirectTarget = FE_RESULT_URL + "?success=false&code=" + responseCode + "&txnRef=" + encode(txnRef);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectTarget));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private String encode(String value) {
        if (value == null) return "";
        try {
            return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}
