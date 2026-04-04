package com.github.TamNguyen.Zob.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;

public interface UserQueryService extends UserReadService {
    ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable);

    User getUserByIdOrThrow(Long id);
}