package com.github.TamNguyen.Zob.service.auth.token;

import org.springframework.security.oauth2.jwt.Jwt;

import com.github.TamNguyen.Zob.domain.response.ResponseLoginDTO;

public interface AuthTokenStrategy {
    String provider();

    String createAccessToken(String email, ResponseLoginDTO dto);

    String createRefreshToken(String email, ResponseLoginDTO dto);

    Jwt checkValidRefreshToken(String token);
}