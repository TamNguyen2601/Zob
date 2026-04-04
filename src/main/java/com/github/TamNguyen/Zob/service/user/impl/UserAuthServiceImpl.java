package com.github.TamNguyen.Zob.service.user.impl;

import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseChangePasswordDTO;
import com.github.TamNguyen.Zob.repository.UserRepository;
import com.github.TamNguyen.Zob.service.user.PasswordPolicyService;
import com.github.TamNguyen.Zob.service.user.UserAuthService;
import com.github.TamNguyen.Zob.service.user.UserMappingService;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.error.NotFoundException;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final UserQueryService userQueryService;
    private final UserRepository userRepository;
    private final PasswordPolicyService passwordPolicyService;
    private final UserMappingService userMapper;

    public UserAuthServiceImpl(UserQueryService userQueryService,
            UserRepository userRepository,
            PasswordPolicyService passwordPolicyService,
            UserMappingService userMapper) {
        this.userQueryService = userQueryService;
        this.userRepository = userRepository;
        this.passwordPolicyService = passwordPolicyService;
        this.userMapper = userMapper;
    }

    @Override
    public void updateUserToken(String token, String email) {
        this.userQueryService.findByEmail(email).ifPresent(currentUser -> {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        });
    }

    @Override
    public ResponseChangePasswordDTO changePasswordForUser(String email, ReqChangePasswordDTO changePasswordDTO) {
        User currentUser = this.userQueryService.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String encodedPassword = this.passwordPolicyService.validateAndEncodeNewPassword(currentUser,
                changePasswordDTO);
        currentUser.setPassword(encodedPassword);
        User updated = this.userRepository.save(currentUser);
        return this.userMapper.toChangePasswordDTO(updated);
    }

    @Override
    public ResponseChangePasswordDTO changePasswordForCurrentUser(ReqChangePasswordDTO changePasswordDTO) {
        String currentUserEmail = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new ValidationErrorException("Unauthorized"));
        return this.changePasswordForUser(currentUserEmail, changePasswordDTO);
    }
}