package com.github.TamNguyen.Zob.service.user;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUpdateUserDTO;

public interface UserCommandService {
    ResponseCreateUserDTO createAndMapUser(User user);

    ResponseUpdateUserDTO updateAndMapUser(ReqUpdateUserDTO reqUser);

    void deleteUserByIdOrThrow(Long id);
}