package com.github.TamNguyen.Zob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.TamNguyen.Zob.domain.request.ReqMomoIpnDTO;
import com.github.TamNguyen.Zob.service.payment.momo.MomoIpnService;
import com.github.TamNguyen.Zob.service.payment.momo.MomoProperties;
import com.github.TamNguyen.Zob.util.annotation.ApiMessage;

@RequestMapping("/api/v1")
@RestController
public class MomoPaymentController {

    private final MomoIpnService momoIpnService;
    private final MomoProperties momoProperties;

    public MomoPaymentController(MomoIpnService momoIpnService, MomoProperties momoProperties) {
        this.momoIpnService = momoIpnService;
        this.momoProperties = momoProperties;
    }

    @PostMapping("/payments/momo/ipn")
    @ApiMessage("MoMo IPN callback")
    public ResponseEntity<String> ipn(@RequestBody ReqMomoIpnDTO body) {
        String rawSignature = buildRawSignature(body);
        try {
            momoIpnService.handleIpn(
                    body.getOrderId(),
                    body.getAmount() != null ? body.getAmount() : 0L,
                    body.getResultCode() != null ? body.getResultCode() : -1,
                    rawSignature,
                    body.getSignature());
        } catch (Exception ex) {
            // Theo chuẩn MoMo: IPN phải luôn trả HTTP 200.
            // Lỗi được log nội bộ, không throw ra ngoài để MoMo không retry vô hạn.
            System.err.println("[MoMo IPN] Xử lý thất bại cho orderId="
                    + body.getOrderId() + " | Lỗi: " + ex.getMessage());
        }
        return ResponseEntity.ok("OK");
    }

    private String buildRawSignature(ReqMomoIpnDTO body) {
        String extraData = body.getExtraData() != null ? body.getExtraData() : "";
        String message = body.getMessage() != null ? body.getMessage() : "";
        String orderId = body.getOrderId() != null ? body.getOrderId() : "";
        String orderInfo = body.getOrderInfo() != null ? body.getOrderInfo() : "";
        String orderType = body.getOrderType() != null ? body.getOrderType() : "";
        String partnerCode = body.getPartnerCode() != null ? body.getPartnerCode() : "";
        String payType = body.getPayType() != null ? body.getPayType() : "";
        String requestId = body.getRequestId() != null ? body.getRequestId() : "";

        String accessKey = StringUtils.hasText(momoProperties.getAccessKey()) ? momoProperties.getAccessKey() : "";

        long amount = body.getAmount() != null ? body.getAmount() : 0L;
        int resultCode = body.getResultCode() != null ? body.getResultCode() : -1;
        long responseTime = body.getResponseTime() != null ? body.getResponseTime() : 0L;
        long transId = body.getTransId() != null ? body.getTransId() : 0L;

        return "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&message=" + message
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&orderType=" + orderType
                + "&partnerCode=" + partnerCode
                + "&payType=" + payType
                + "&requestId=" + requestId
                + "&responseTime=" + responseTime
                + "&resultCode=" + resultCode
                + "&transId=" + transId;
    }
}
