package com.github.TamNguyen.Zob.service.user;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResponseChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseLoginDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUserDTO;

public interface UserMappingService {
    ResponseCreateUserDTO toCreateUserDTO(User user);

    ResponseUserDTO toUserDTO(User user);

    ResponseUpdateUserDTO toUpdateUserDTO(User user);

    ResponseChangePasswordDTO toChangePasswordDTO(User user);

    ResponseLoginDTO.UserLogin toLoginUser(User user);

    ResponseLoginDTO.UserGetAccount toUserGetAccount(User user);
}