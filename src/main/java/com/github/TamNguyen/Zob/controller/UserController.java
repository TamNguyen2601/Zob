package com.github.TamNguyen.Zob.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.github.TamNguyen.Zob.domain.request.ReqUpdateMyProfileDTO;
import com.github.TamNguyen.Zob.domain.request.ReqUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseChangePasswordDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.service.user.UserAuthService;
import com.github.TamNguyen.Zob.service.user.UserCommandService;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RequestMapping("/api/v1")
@RestController
public class UserController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final UserAuthService userAuthService;

    public UserController(UserQueryService userQueryService,
            UserCommandService userCommandService,
            UserAuthService userAuthService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
        this.userAuthService = userAuthService;
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(
                this.userQueryService.fetchAllUser(spec, pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userQueryService.getUserByIdOrThrow(id));
    }

    @PostMapping("/users")
    @ApiMessage("Create new user")
    public ResponseEntity<ResponseCreateUserDTO> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userCommandService.createAndMapUser(user));
    }

    @PutMapping("/users")
    public ResponseEntity<ResponseUpdateUserDTO> updateUser(@Valid @RequestBody ReqUpdateUserDTO user) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userCommandService.updateAndMapUser(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id) {
        this.userCommandService.deleteUserByIdOrThrow(id);
        return ResponseEntity.ok().body(null);

    }

    @PostMapping("/auth/change-password")
    @ApiMessage("Change password for current user")
    public ResponseEntity<ResponseChangePasswordDTO> changePassword(
            @Valid @RequestBody ReqChangePasswordDTO changePasswordDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userAuthService.changePasswordForCurrentUser(changePasswordDTO));
    }

    @GetMapping("/users/me")
    @ApiMessage("Get current user's profile")
    public ResponseEntity<User> getCurrentUserProfile() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userQueryService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.status(HttpStatus.OK).body(currentUser);
    }

    @PutMapping("/users/me")
    @ApiMessage("Update current user's profile")
    public ResponseEntity<ResponseUpdateUserDTO> updateCurrentUserProfile(
            @Valid @RequestBody ReqUpdateMyProfileDTO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userCommandService.updateMyProfile(user));
    }
}
