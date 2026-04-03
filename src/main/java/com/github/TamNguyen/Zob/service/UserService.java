package com.github.TamNguyen.Zob.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.TamNguyen.Zob.domain.Company;
import com.github.TamNguyen.Zob.domain.Role;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.request.ReqUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.domain.Resume;
import com.github.TamNguyen.Zob.repository.ResumeRepository;
import com.github.TamNguyen.Zob.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final ResumeRepository resumeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService,
            ResumeRepository resumeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
        this.resumeRepository = resumeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResponseUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    public User findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        return this.userRepository.save(user);
    }

    @Transactional
    public void handleDeleteUser(Long id) {
        User currentUser = this.findUserById(id);
        if (currentUser != null) {
            List<Resume> resumes = this.resumeRepository.findByUser(currentUser);
            if (resumes != null && !resumes.isEmpty()) {
                this.resumeRepository.deleteAll(resumes);
            }
        }
        userRepository.deleteById(id);
    }

    public User handleUpdateUser(ReqUpdateUserDTO reqUser) {
        User currentUser = this.findUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            if (reqUser.getAge() != null) {
                currentUser.setAge(reqUser.getAge());
            }
            currentUser.setName(reqUser.getName());

            // check company
            if (reqUser.getCompany() != null && reqUser.getCompany().getId() != null) {
                Optional<Company> companyOptional = this.companyService.findById(reqUser.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            // check role
            if (reqUser.getRole() != null && reqUser.getRole().getId() != null) {
                Role r = this.roleService.fetchById(reqUser.getRole().getId());
                currentUser.setRole(r != null ? r : null);
            }

            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User findUserByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResponseCreateUserDTO convertToResCreateUserDTO(User user) {
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

    public ResponseUserDTO convertToResUserDTO(User user) {
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

    public ResponseUpdateUserDTO convertToResUpdateUserDTO(User user) {
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

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    /**
     * Change password for the current user
     * 
     * @param user              The current user to change password
     * @param changePasswordDTO The DTO containing old password and new password
     * @return The updated user with new password
     * @throws IllegalArgumentException if old password is invalid or new passwords
     *                                  don't match
     */
    public User handleChangePassword(User user, ReqChangePasswordDTO changePasswordDTO) {
        // Verify old password
        if (!this.passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Verify new password and confirmation match
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("New password and confirmation password do not match");
        }

        // Verify new password is different from old password
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from old password");
        }

        // Encode and set new password
        String encodedPassword = this.passwordEncoder.encode(changePasswordDTO.getNewPassword());
        user.setPassword(encodedPassword);

        // Save updated user
        return this.userRepository.save(user);
    }

    public ResponseChangePasswordDTO convertToResChangePasswordDTO(User user) {
        ResponseChangePasswordDTO res = new ResponseChangePasswordDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

}
