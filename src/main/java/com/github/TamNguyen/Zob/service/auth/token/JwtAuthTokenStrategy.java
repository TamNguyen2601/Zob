package com.github.TamNguyen.Zob.service.auth.token;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.response.ResponseLoginDTO;
import com.github.TamNguyen.Zob.util.SecurityUtil;

@Component
public class JwtAuthTokenStrategy implements AuthTokenStrategy {

    private final SecurityUtil securityUtil;

    public JwtAuthTokenStrategy(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    @Override
    public String provider() {
        return "jwt";
    }

    @Override
    public String createAccessToken(String email, ResponseLoginDTO dto) {
        return this.securityUtil.createAccessToken(email, dto);
    }

    @Override
    public String createRefreshToken(String email, ResponseLoginDTO dto) {
        return this.securityUtil.createRefreshToken(email, dto);
    }

    @Override
    public Jwt checkValidRefreshToken(String token) {
        return this.securityUtil.checkValidRefreshToken(token);
    }
}