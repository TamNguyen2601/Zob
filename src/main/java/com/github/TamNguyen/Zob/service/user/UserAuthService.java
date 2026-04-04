package com.github.TamNguyen.Zob.service.user;

import com.github.TamNguyen.Zob.domain.request.ReqChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseChangePasswordDTO;

public interface UserAuthService {
    void updateUserToken(String token, String email);

    ResponseChangePasswordDTO changePasswordForUser(String email, ReqChangePasswordDTO changePasswordDTO);

    ResponseChangePasswordDTO changePasswordForCurrentUser(ReqChangePasswordDTO changePasswordDTO);
}