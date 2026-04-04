package com.github.TamNguyen.Zob.service.auth;

import org.springframework.http.ResponseCookie;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqLoginDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseLoginDTO;

public interface AuthApplicationService {

    AuthResponse login(ReqLoginDTO loginDto);

    ResponseLoginDTO.UserGetAccount getCurrentAccount();

    AuthResponse refresh(String refreshToken);

    ResponseCookie logout();

    ResponseCreateUserDTO register(User postManUser);

    record AuthResponse(ResponseLoginDTO body, ResponseCookie cookie) {
    }
}