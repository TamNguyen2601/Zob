package com.github.TamNguyen.Zob.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.github.TamNguyen.Zob.domain.request.ReqChatDTO;
import com.github.TamNguyen.Zob.domain.response.ResChatDTO;
import com.github.TamNguyen.Zob.util.error.GeminiException;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.api-url}")
    private String apiUrl;

    private final RestClient restClient;

    /**
     * System prompt định hướng chatbot chuyên về HR / tuyển dụng
     */
    private static final String SYSTEM_PROMPT =
            "Bạn là trợ lý tuyển dụng thông minh của nền tảng Zob - nơi kết nối ứng viên và nhà tuyển dụng. " +
            "Nhiệm vụ của bạn là hỗ trợ người dùng trong các vấn đề liên quan đến tuyển dụng như: " +
            "tư vấn viết CV, chuẩn bị phỏng vấn, gợi ý công việc phù hợp, giải đáp thắc mắc về lương thưởng và phúc lợi. " +
            "Hãy trả lời bằng tiếng Việt, thân thiện, ngắn gọn và chuyên nghiệp. " +
            "Nếu câu hỏi không liên quan đến tuyển dụng hoặc công việc, hãy lịch sự hướng người dùng quay lại chủ đề chính.";

    public GeminiService() {
        this.restClient = RestClient.create();
    }

    /**
     * Gửi message đến Gemini API và nhận reply.
     *
     * @param request ReqChatDTO chứa message và history (tùy chọn)
     * @return ResChatDTO với nội dung reply từ AI
     */
    @SuppressWarnings("unchecked")
    public ResChatDTO chat(ReqChatDTO request) {
        String endpoint = String.format("%s/%s:generateContent?key=%s", apiUrl, model, apiKey);

        // Xây dựng danh sách contents (history + message hiện tại)
        List<Map<String, Object>> contents = buildContents(request);

        // Xây dựng request body
        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", SYSTEM_PROMPT))),
                "contents", contents,
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 1024));

        log.info("[Gemini] Gửi request đến model={} | message='{}'", model, request.getMessage());

        try {
            Map<String, Object> response = restClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            String reply = extractReply(response);
            log.info("[Gemini] Nhận reply thành công | length={}", reply.length());
            return new ResChatDTO(reply, model);

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("[Gemini] ⚠️ Rate limit (429) - Vui lòng thử lại sau");
            throw new GeminiException(
                    "Trợ lý AI đang bận, vui lòng thử lại sau vài giây.",
                    HttpStatus.TOO_MANY_REQUESTS, e);

        } catch (HttpClientErrorException e) {
            log.error("[Gemini] ❌ HTTP lỗi {}: {}", e.getStatusCode(), e.getMessage());
            throw new GeminiException(
                    "Yêu cầu đến Gemini AI không hợp lệ: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST, e);

        } catch (RestClientException e) {
            log.error("[Gemini] ❌ Lỗi kết nối đến Gemini: {}", e.getMessage(), e);
            throw new GeminiException(
                    "Không thể kết nối đến Gemini AI. Vui lòng thử lại sau.",
                    HttpStatus.SERVICE_UNAVAILABLE, e);

        } catch (Exception e) {
            log.error("[Gemini] ❌ Lỗi không xác định: {}", e.getMessage(), e);
            throw new GeminiException(
                    "Đã xảy ra lỗi khi xử lý yêu cầu AI.",
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    /**
     * Xây dựng danh sách contents từ history + message mới.
     */
    private List<Map<String, Object>> buildContents(ReqChatDTO request) {
        List<Map<String, Object>> contents = new ArrayList<>();

        // Thêm history (nếu có)
        if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            for (ReqChatDTO.HistoryItem item : request.getHistory()) {
                contents.add(Map.of(
                        "role", item.getRole(),
                        "parts", List.of(Map.of("text", item.getText()))));
            }
        }

        // Thêm message hiện tại của user
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", request.getMessage()))));

        return contents;
    }

    /**
     * Parse và lấy text reply từ response JSON của Gemini.
     */
    @SuppressWarnings("unchecked")
    private String extractReply(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) response.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("Gemini API không trả về candidates");
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("Gemini API không trả về parts");
            }

            return (String) parts.get(0).get("text");

        } catch (ClassCastException | NullPointerException e) {
            log.error("[Gemini] Lỗi parse response: {}", e.getMessage());
            throw new RuntimeException("Không thể đọc phản hồi từ Gemini API", e);
        }
    }
}
