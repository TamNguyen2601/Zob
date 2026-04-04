package com.github.TamNguyen.Zob.service.auth;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqLoginDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseLoginDTO;
import com.github.TamNguyen.Zob.service.auth.token.AuthTokenStrategy;
import com.github.TamNguyen.Zob.service.user.UserAuthService;
import com.github.TamNguyen.Zob.service.user.UserCommandService;
import com.github.TamNguyen.Zob.service.user.UserMappingService;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.error.NotFoundException;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class AuthService implements AuthApplicationService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final UserAuthService userAuthService;
    private final UserMappingService userMapper;
    private final Map<String, AuthTokenStrategy> tokenStrategyByProvider;

    @Value("${zob.auth.token-provider:jwt}")
    private String activeTokenProvider;

    @Value("${zob.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthService(AuthenticationManagerBuilder authenticationManagerBuilder,
            UserQueryService userQueryService,
            UserCommandService userCommandService,
            UserAuthService userAuthService,
            UserMappingService userMapper,
            List<AuthTokenStrategy> tokenStrategies) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
        this.userAuthService = userAuthService;
        this.userMapper = userMapper;
        this.tokenStrategyByProvider = tokenStrategies.stream()
                .collect(Collectors.toMap(AuthTokenStrategy::provider, strategy -> strategy));
    }

    @Override
    public AuthResponse login(ReqLoginDTO loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = this.userQueryService.findByEmail(loginDto.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        ResponseLoginDTO response = buildLoginResponse(currentUser, authentication.getName());
        String refreshToken = getTokenStrategy().createRefreshToken(loginDto.getUsername(), response);
        this.userAuthService.updateUserToken(refreshToken, loginDto.getUsername());

        return new AuthResponse(response, buildRefreshCookie(refreshToken));
    }

    @Override
    public ResponseLoginDTO.UserGetAccount getCurrentAccount() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isBlank()) {
            return new ResponseLoginDTO.UserGetAccount();
        }

        return this.userQueryService.findByEmail(email)
                .map(this.userMapper::toUserGetAccount)
                .orElse(new ResponseLoginDTO.UserGetAccount());
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if ("abc".equals(refreshToken)) {
            throw new ValidationErrorException("Bạn không có refresh token ở cookie");
        }

        Jwt decodedToken = getTokenStrategy().checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        User currentUser = this.userQueryService.findByRefreshTokenAndEmail(refreshToken, email)
                .orElseThrow(() -> new ValidationErrorException("Refresh Token không hợp lệ"));

        ResponseLoginDTO response = buildLoginResponse(currentUser, email);
        String newRefreshToken = getTokenStrategy().createRefreshToken(email, response);
        this.userAuthService.updateUserToken(newRefreshToken, email);

        return new AuthResponse(response, buildRefreshCookie(newRefreshToken));
    }

    @Override
    public ResponseCookie logout() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if ("".equals(email)) {
            throw new ValidationErrorException("Access Token không hợp lệ");
        }

        this.userAuthService.updateUserToken(null, email);
        return ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    @Override
    public ResponseCreateUserDTO register(User postManUser) {
        return this.userCommandService.createAndMapUser(postManUser);
    }

    private ResponseLoginDTO buildLoginResponse(User user, String subject) {
        ResponseLoginDTO response = new ResponseLoginDTO();
        if (user != null) {
            response.setUser(this.userMapper.toLoginUser(user));
        }

        String accessToken = getTokenStrategy().createAccessToken(subject, response);
        response.setAccessToken(accessToken);
        return response;
    }

    private AuthTokenStrategy getTokenStrategy() {
        AuthTokenStrategy strategy = this.tokenStrategyByProvider.get(activeTokenProvider);
        if (strategy == null) {
            throw new IllegalStateException("No token strategy configured for provider: " + activeTokenProvider);
        }
        return strategy;
    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
    }

}