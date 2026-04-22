package com.github.TamNguyen.Zob.service.payment.vnpay;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class VnpayPaymentClient {

    private static final Logger log = LoggerFactory.getLogger(VnpayPaymentClient.class);


    private static final DateTimeFormatter VNPAY_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VnpayProperties vnpayProperties;

    public VnpayPaymentClient(VnpayProperties vnpayProperties) {
        this.vnpayProperties = vnpayProperties;
    }

    /**
     * Tạo payment URL để FE mở/trình duyệt redirect.
     * VNPay yêu cầu amount = VND * 100.
     */
    public String createPaymentUrl(String txnRef, long amountVnd, String orderInfo, String clientIp) {
        validateConfig();

        if (!StringUtils.hasText(txnRef)) {
            throw new ValidationErrorException("Thiếu vnp_TxnRef");
        }
        if (amountVnd <= 0) {
            throw new ValidationErrorException("Amount phải > 0");
        }

        String safeOrderInfo = StringUtils.hasText(orderInfo) ? orderInfo : "Payment";
        String ip = normalizeIp(clientIp);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        String createDate = VNPAY_DATE.format(now);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", vnpayProperties.getVersion());
        params.put("vnp_Command", vnpayProperties.getCommand());
        params.put("vnp_TmnCode", vnpayProperties.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amountVnd * 100));
        params.put("vnp_CurrCode", vnpayProperties.getCurrCode());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", safeOrderInfo);
        params.put("vnp_OrderType", vnpayProperties.getOrderType());
        params.put("vnp_Locale", vnpayProperties.getLocale());
        params.put("vnp_ReturnUrl", vnpayProperties.getReturnUrl());
        params.put("vnp_IpAddr", ip);
        params.put("vnp_CreateDate", createDate);

        SignedQuery signed = buildSignedQuery(params);
        String fullQuery = signed.queryString() + "&vnp_SecureHash=" + urlEncodeQuery(signed.secureHash());
        return vnpayProperties.getPayUrl() + "?" + fullQuery;
    }

    public boolean verifySignature(Map<String, String> allParams) {
        validateConfig();
        if (allParams == null || allParams.isEmpty()) {
            return false;
        }

        String providedHash = allParams.get("vnp_SecureHash");
        if (!StringUtils.hasText(providedHash)) {
            return false;
        }

        Map<String, String> paramsToSign = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (!StringUtils.hasText(key)) {
                continue;
            }
            if ("vnp_SecureHash".equalsIgnoreCase(key) || "vnp_SecureHashType".equalsIgnoreCase(key)) {
                continue;
            }
            String value = entry.getValue();
            if (value == null) {
                value = "";
            }

            // VNPay sample: bỏ các field rỗng khỏi hashData
            if (!StringUtils.hasText(value)) {
                continue;
            }
            paramsToSign.put(key, value);
        }

        SignedQuery signed = buildSignedQuery(paramsToSign);
        String expectedHash = signed.secureHash();
        return providedHash.equalsIgnoreCase(expectedHash);
    }

    private record SignedQuery(String queryString, String secureHash) {
    }

    /**
     * Build query string and secure hash following VNPay official sample:
     * - sort by field name alphabetically
     * - skip empty values
     * - hashData  : key=urlEncodeHash(value)  — dùng US_ASCII, giữ '+' cho space
     *               (khớp với PHP urlencode() và Java sample của VNPay)
     * - queryString: key=urlEncodeQuery(value) — dùng UTF-8, đổi '+' → '%20'
     */
    private SignedQuery buildSignedQuery(Map<String, String> params) {
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        params.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(e -> {
                    String key = e.getKey();
                    String value = e.getValue();
                    if (!StringUtils.hasText(key) || !StringUtils.hasText(value)) {
                        return;
                    }

                    if (hashData.length() > 0) {
                        hashData.append('&');
                        query.append('&');
                    }

                    // hashData: dùng US_ASCII — giữ '+' cho space, khớp với VNPay server
                    hashData.append(key).append('=').append(urlEncodeHash(value));
                    // queryString: dùng UTF-8 với %20 — chuẩn URL
                    query.append(urlEncodeQuery(key)).append('=').append(urlEncodeQuery(value));
                });

        String hashDataStr = hashData.toString();
        log.info("[VNPay] TmnCode   : {}", vnpayProperties.getTmnCode());
        log.info("[VNPay] HashSecret : {}", vnpayProperties.getHashSecret());
        log.info("[VNPay] HashData   : {}", hashDataStr);
        String secureHash = hmacSha512(vnpayProperties.getHashSecret(), hashDataStr);
        log.info("[VNPay] SecureHash : {}", secureHash);
        return new SignedQuery(query.toString(), secureHash);
    }

    /**
     * Encode cho hashData: dùng US_ASCII, GIỮ NGUYÊN '+' cho space.
     * Phải khớp với cách VNPay server (PHP urlencode / Java US_ASCII) re-encode khi verify.
     */
    private String urlEncodeHash(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

    /**
     * Encode cho query string URL: dùng UTF-8, đổi '+' → '%20' cho đúng chuẩn URL.
     */
    private String urlEncodeQuery(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    private String normalizeIp(String rawIp) {
        if (!StringUtils.hasText(rawIp)) {
            return "127.0.0.1";
        }

        // In case of proxy header "client, proxy1, proxy2"
        String ip = rawIp.split(",")[0].trim();
        if (!StringUtils.hasText(ip)) {
            return "127.0.0.1";
        }

        // Common localhost IPv6
        if ("::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "127.0.0.1";
        }

        // IPv4-mapped IPv6 address
        if (ip.startsWith("::ffff:")) {
            ip = ip.substring("::ffff:".length());
        }

        // VNPay thường mong IPv4; nếu là IPv6 thì fallback IPv4 loopback
        if (ip.contains(":")) {
            return "127.0.0.1";
        }

        return ip;
    }

    private void validateConfig() {
        if (!StringUtils.hasText(vnpayProperties.getPayUrl())
                || !StringUtils.hasText(vnpayProperties.getReturnUrl())
                || !StringUtils.hasText(vnpayProperties.getIpnUrl())
                || !StringUtils.hasText(vnpayProperties.getTmnCode())
                || !StringUtils.hasText(vnpayProperties.getHashSecret())) {
            throw new ValidationErrorException(
                    "Thiếu cấu hình VNPay (vnpay.pay-url/return-url/ipn-url/tmn-code/hash-secret)");
        }
    }

    private String hmacSha512(String secret, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKeySpec);
            byte[] raw = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (Exception e) {
            throw new RuntimeException("Cannot sign/verify VNPay signature", e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
