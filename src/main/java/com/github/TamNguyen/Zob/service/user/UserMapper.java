package com.github.TamNguyen.Zob.service.user;

import org.springframework.stereotype.Component;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.response.ResponseChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseLoginDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUserDTO;

@Component
public class UserMapper implements UserMappingService {

    @Override
    public ResponseCreateUserDTO toCreateUserDTO(User user) {
        ResponseCreateUserDTO res = new ResponseCreateUserDTO();
        ResponseCreateUserDTO.CompanyUser com = new ResponseCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }
        return res;
    }

    @Override
    public ResponseUserDTO toUserDTO(User user) {
        ResponseUserDTO res = new ResponseUserDTO();
        ResponseUserDTO.CompanyUser com = new ResponseUserDTO.CompanyUser();
        ResponseUserDTO.RoleUser roleUser = new ResponseUserDTO.RoleUser();

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    @Override
    public ResponseUpdateUserDTO toUpdateUserDTO(User user) {
        ResponseUpdateUserDTO res = new ResponseUpdateUserDTO();
        ResponseUpdateUserDTO.CompanyUser com = new ResponseUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    @Override
    public ResponseChangePasswordDTO toChangePasswordDTO(User user) {
        ResponseChangePasswordDTO res = new ResponseChangePasswordDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    @Override
    public ResponseLoginDTO.UserLogin toLoginUser(User user) {
        return new ResponseLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole());
    }

    @Override
    public ResponseLoginDTO.UserGetAccount toUserGetAccount(User user) {
        ResponseLoginDTO.UserAccount userAccount = new ResponseLoginDTO.UserAccount();
        userAccount.setId(user.getId());
        userAccount.setEmail(user.getEmail());
        userAccount.setName(user.getName());
        userAccount.setAge(user.getAge());
        userAccount.setGender(user.getGender());
        userAccount.setAddress(user.getAddress());
        userAccount.setRole(user.getRole());

        ResponseLoginDTO.UserGetAccount userGetAccount = new ResponseLoginDTO.UserGetAccount();
        userGetAccount.setUser(userAccount);
        return userGetAccount;
    }
}