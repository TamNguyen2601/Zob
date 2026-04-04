package com.github.TamNguyen.Zob.service.user;

import java.util.Optional;

import com.github.TamNguyen.Zob.domain.User;

public interface UserReadService {
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshTokenAndEmail(String token, String email);
}