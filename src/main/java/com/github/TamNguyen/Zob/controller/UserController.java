package com.github.TamNguyen.Zob.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.request.ReqUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.service.UserService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.annotation.ApiMessage;
import com.github.TamNguyen.Zob.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RequestMapping("/api/v1")
@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(
                this.userService.fetchAllUser(spec, pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) throws IdInvalidException {
        User findUser = this.userService.findUserById(id);
        if (findUser == null) {
            throw new IdInvalidException("id khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK).body(findUser);
    }

    @PostMapping("/users")
    @ApiMessage("Create new user")
    public ResponseEntity<ResponseCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("email da ton tai");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @PutMapping("/users")
    public ResponseEntity<ResponseUpdateUserDTO> updateUser(@Valid @RequestBody ReqUpdateUserDTO user)
            throws IdInvalidException {
        User update = this.userService.handleUpdateUser(user);
        if (update == null) {
            throw new IdInvalidException("id khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateUserDTO(update));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id) throws IdInvalidException {
        User findUser = this.userService.findUserById(id);
        if (findUser == null) {
            throw new IdInvalidException("id khong ton tai");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok().body(null);

    }

    @PostMapping("/auth/change-password")
    @ApiMessage("Change password for current user")
    public ResponseEntity<ResponseChangePasswordDTO> changePassword(
            @Valid @RequestBody ReqChangePasswordDTO changePasswordDTO) throws IdInvalidException {
        // Get current user from security context
        String currentUserEmail = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Unauthorized"));

        User currentUser = this.userService.handleGetUserByUsername(currentUserEmail);
        if (currentUser == null) {
            throw new IdInvalidException("User not found");
        }

        // Change password
        try {
            User updatedUser = this.userService.handleChangePassword(currentUser, changePasswordDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.userService.convertToResChangePasswordDTO(updatedUser));
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException(e.getMessage());
        }
    }
}
