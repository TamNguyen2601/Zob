package com.github.TamNguyen.Zob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.TamNguyen.Zob.domain.request.ReqChatDTO;
import com.github.TamNguyen.Zob.domain.response.ResChatDTO;
import com.github.TamNguyen.Zob.service.GeminiService;
import com.github.TamNguyen.Zob.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    /**
     * Gửi message đến Gemini AI và nhận phản hồi.
     * Yêu cầu JWT Bearer token (user đã đăng nhập).
     *
     * POST /api/v1/chat
     * Body: { "message": "...", "history": [...] }
     */
    @PostMapping("/chat")
    @ApiMessage("Chat with Gemini AI")
    public ResponseEntity<ResChatDTO> chat(@Valid @RequestBody ReqChatDTO request) {
        ResChatDTO response = geminiService.chat(request);
        return ResponseEntity.ok(response);
    }
}
