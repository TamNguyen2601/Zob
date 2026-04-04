package com.github.TamNguyen.Zob.service.user.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResponseUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.repository.UserRepository;
import com.github.TamNguyen.Zob.service.user.UserMappingService;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.error.NotFoundException;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserMappingService userMapper;

    public UserQueryServiceImpl(UserRepository userRepository, UserMappingService userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        List<ResponseUserDTO> listUser = pageUser.getContent()
                .stream().map(this.userMapper::toUserDTO)
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    @Override
    public Optional<User> findById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(this.userRepository.findByEmail(email));
    }

    @Override
    public Optional<User> findByRefreshTokenAndEmail(String token, String email) {
        return Optional.ofNullable(this.userRepository.findByRefreshTokenAndEmail(token, email));
    }

    @Override
    public User getUserByIdOrThrow(Long id) {
        return this.findById(id).orElseThrow(() -> new NotFoundException("id khong ton tai"));
    }
}