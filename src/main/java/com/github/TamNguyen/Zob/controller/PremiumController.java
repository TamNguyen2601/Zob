package com.github.TamNguyen.Zob.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqPremiumPurchaseDTO;
import com.github.TamNguyen.Zob.domain.response.ResPremiumMeDTO;
import com.github.TamNguyen.Zob.domain.response.ResPremiumPurchaseDTO;
import com.github.TamNguyen.Zob.service.premium.PremiumPurchaseService;
import com.github.TamNguyen.Zob.service.premium.PremiumQueryService;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RequestMapping("/api/v1")
@RestController
public class PremiumController {

    private final UserQueryService userQueryService;
    private final PremiumPurchaseService premiumPurchaseService;
    private final PremiumQueryService premiumQueryService;

    public PremiumController(
            UserQueryService userQueryService,
            PremiumPurchaseService premiumPurchaseService,
            PremiumQueryService premiumQueryService) {
        this.userQueryService = userQueryService;
        this.premiumPurchaseService = premiumPurchaseService;
        this.premiumQueryService = premiumQueryService;
    }

    @PostMapping("/premium/purchase")
    @ApiMessage("Create premium purchase transaction")
    public ResponseEntity<ResPremiumPurchaseDTO> purchase(@Valid @RequestBody ReqPremiumPurchaseDTO body) {
        User currentUser = getCurrentUserOrThrow();
        ResPremiumPurchaseDTO res = premiumPurchaseService.purchase(currentUser, body.getPlanCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/premium/me")
    @ApiMessage("Get current premium status")
    public ResponseEntity<ResPremiumMeDTO> me() {
        User currentUser = getCurrentUserOrThrow();
        ResPremiumMeDTO res = premiumQueryService.getPremiumStatus(currentUser, Instant.now());
        return ResponseEntity.ok(res);
    }

    private User getCurrentUserOrThrow() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        return this.userQueryService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
