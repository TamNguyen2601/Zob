package com.github.TamNguyen.Zob.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqChangePasswordDTO;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class PasswordPolicyService {

    private final PasswordEncoder passwordEncoder;

    public PasswordPolicyService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String validateAndEncodeNewPassword(User user, ReqChangePasswordDTO changePasswordDTO)
            throws ValidationErrorException {
        if (!this.passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new ValidationErrorException("Old password is incorrect");
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getNewPasswordConfirm())) {
            throw new ValidationErrorException("New password and confirmation password do not match");
        }

        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new ValidationErrorException("New password must be different from old password");
        }

        return this.passwordEncoder.encode(changePasswordDTO.getNewPassword());
    }
}