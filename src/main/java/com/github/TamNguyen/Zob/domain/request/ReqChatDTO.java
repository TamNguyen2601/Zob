package com.github.TamNguyen.Zob.domain.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class ReqChatDTO {

    @NotBlank(message = "Message is required")
    private String message;

    /**
     * Lịch sử hội thoại (stateless - client tự quản lý và gửi lên).
     * Mỗi phần tử có dạng: { "role": "user"/"model", "text": "..." }
     */
    private List<HistoryItem> history;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HistoryItem> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryItem> history) {
        this.history = history;
    }

    public static class HistoryItem {
        private String role;  // "user" hoặc "model"
        private String text;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
