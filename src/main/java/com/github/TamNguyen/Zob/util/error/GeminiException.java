package com.github.TamNguyen.Zob.util.error;

import org.springframework.http.HttpStatus;

/**
 * Exception dành riêng cho lỗi gọi Gemini AI API.
 * Cho phép trả về HTTP status code phù hợp (429, 503...).
 */
public class GeminiException extends RuntimeException {

    private final HttpStatus httpStatus;

    public GeminiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public GeminiException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
