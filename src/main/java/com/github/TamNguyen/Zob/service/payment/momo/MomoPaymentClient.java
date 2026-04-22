package com.github.TamNguyen.Zob.service.payment.momo;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

import lombok.Getter;
import lombok.Setter;

@Service
public class MomoPaymentClient {

    private final MomoProperties momoProperties;
    private final RestClient restClient;

    public MomoPaymentClient(MomoProperties momoProperties, RestClient.Builder restClientBuilder) {
        this.momoProperties = momoProperties;
        this.restClient = restClientBuilder.build();
    }

    /**
     * payUrl      : link web dùng để tạo QR ở FE (bất kỳ QR scanner nào đọc được)
     * momoDeeplink: deep link momo://... dùng để mở thẳng app MoMo (optional)
     */
    public record CreatePaymentResult(String payUrl, String momoDeeplink) {
    }

    public CreatePaymentResult createPayment(String orderId, long amountVnd, String orderInfo) {
        validateConfig();

        String requestId = UUID.randomUUID().toString();
        String extraData = "";
        String requestType = StringUtils.hasText(momoProperties.getRequestType())
                ? momoProperties.getRequestType()
                : "captureWallet";

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("partnerCode", momoProperties.getPartnerCode());
        payload.put("accessKey", momoProperties.getAccessKey());
        payload.put("requestId", requestId);
        payload.put("amount", String.valueOf(amountVnd));
        payload.put("orderId", orderId);
        payload.put("orderInfo", orderInfo);
        payload.put("redirectUrl", momoProperties.getRedirectUrl());
        payload.put("ipnUrl", momoProperties.getIpnUrl());
        payload.put("extraData", extraData);
        payload.put("requestType", requestType);

        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + amountVnd
                + "&extraData=" + extraData
                + "&ipnUrl=" + momoProperties.getIpnUrl()
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + momoProperties.getPartnerCode()
                + "&redirectUrl=" + momoProperties.getRedirectUrl()
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = hmacSha256(momoProperties.getSecretKey(), rawSignature);
        payload.put("signature", signature);

        MomoCreateResponse response = restClient.post()
                .uri(momoProperties.getEndpoint())
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(MomoCreateResponse.class);

        if (response == null) {
            throw new ValidationErrorException("Không nhận được response từ MoMo");
        }
        if (response.getResultCode() != null && response.getResultCode() != 0) {
            throw new ValidationErrorException("MoMo create payment thất bại: " + response.getMessage());
        }
        if (!StringUtils.hasText(response.getPayUrl())) {
            throw new ValidationErrorException("MoMo response thiếu payUrl");
        }

        return new CreatePaymentResult(response.getPayUrl(), response.getQrCodeUrl());
    }

    public boolean verifyIpnSignature(String rawSignature, String providedSignature) {
        validateConfig();
        if (!StringUtils.hasText(rawSignature) || !StringUtils.hasText(providedSignature)) {
            return false;
        }
        String expected = hmacSha256(momoProperties.getSecretKey(), rawSignature);
        return providedSignature.equalsIgnoreCase(expected);
    }

    private void validateConfig() {
        if (!StringUtils.hasText(momoProperties.getEndpoint())
                || !StringUtils.hasText(momoProperties.getPartnerCode())
                || !StringUtils.hasText(momoProperties.getAccessKey())
                || !StringUtils.hasText(momoProperties.getSecretKey())
                || !StringUtils.hasText(momoProperties.getRedirectUrl())
                || !StringUtils.hasText(momoProperties.getIpnUrl())) {
            throw new ValidationErrorException(
                    "Thiếu cấu hình MoMo (momo.endpoint/partnerCode/accessKey/secretKey/redirectUrl/ipnUrl)");
        }
    }

    private String hmacSha256(String secretKey, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] raw = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (Exception e) {
            throw new RuntimeException("Cannot sign/verify MoMo signature", e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MomoCreateResponse {
        private Integer resultCode;
        private String message;

        /**
         * MoMo đặt tên "qrCodeUrl" nhưng thực ra là deep link momo://...
         * Chỉ app MoMo mới scan được. KHÔNG phải URL ảnh PNG.
         */
        @JsonProperty("qrCodeUrl")
        private String qrCodeUrl;

        /** Link web thanh toán — dùng để tạo QR image ở FE */
        @JsonProperty("payUrl")
        private String payUrl;
    }
}
