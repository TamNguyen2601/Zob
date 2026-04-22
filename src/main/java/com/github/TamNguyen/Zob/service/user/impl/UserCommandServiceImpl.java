package com.github.TamNguyen.Zob.service.user.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.TamNguyen.Zob.domain.Company;
import com.github.TamNguyen.Zob.domain.PremiumSubscription;
import com.github.TamNguyen.Zob.domain.Resume;
import com.github.TamNguyen.Zob.domain.Role;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.domain.request.ReqUpdateMyProfileDTO;
import com.github.TamNguyen.Zob.domain.request.ReqUpdateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseCreateUserDTO;
import com.github.TamNguyen.Zob.domain.response.ResponseUpdateUserDTO;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.repository.PremiumSubscriptionRepository;
import com.github.TamNguyen.Zob.repository.ResumeRepository;
import com.github.TamNguyen.Zob.repository.UserRepository;
import com.github.TamNguyen.Zob.service.CompanyService;
import com.github.TamNguyen.Zob.service.RoleService;
import com.github.TamNguyen.Zob.service.premium.PremiumAccessPolicy;
import com.github.TamNguyen.Zob.service.user.UserCommandService;
import com.github.TamNguyen.Zob.service.user.UserMappingService;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.error.ConflictException;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final ResumeRepository resumeRepository;
    private final PremiumSubscriptionRepository premiumSubscriptionRepository;
    private final PremiumAccessPolicy premiumAccessPolicy;
    private final UserMappingService userMapper;
    private final UserQueryService userQueryService;
    private final PasswordEncoder passwordEncoder;

    public UserCommandServiceImpl(UserRepository userRepository,
            CompanyService companyService,
            RoleService roleService,
            ResumeRepository resumeRepository,
            PremiumSubscriptionRepository premiumSubscriptionRepository,
            PremiumAccessPolicy premiumAccessPolicy,
            UserMappingService userMapper,
            UserQueryService userQueryService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
        this.resumeRepository = resumeRepository;
        this.premiumSubscriptionRepository = premiumSubscriptionRepository;
        this.premiumAccessPolicy = premiumAccessPolicy;
        this.userMapper = userMapper;
        this.userQueryService = userQueryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseCreateUserDTO createAndMapUser(User user) {
        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("email da ton tai");
        }

        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.orElse(null));
        }

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        User newUser = this.userRepository.save(user);
        return this.userMapper.toCreateUserDTO(newUser);
    }

    @Override
    public ResponseUpdateUserDTO updateAndMapUser(ReqUpdateUserDTO reqUser) {
        User currentUser = this.userQueryService.getUserByIdOrThrow(reqUser.getId());

        currentUser.setAddress(reqUser.getAddress());
        currentUser.setGender(reqUser.getGender());
        if (reqUser.getAge() != null) {
            currentUser.setAge(reqUser.getAge());
        }
        currentUser.setName(reqUser.getName());

        if (reqUser.getCompany() != null && reqUser.getCompany().getId() != null) {
            Optional<Company> companyOptional = this.companyService.findById(reqUser.getCompany().getId());
            currentUser.setCompany(companyOptional.orElse(null));
        }

        if (reqUser.getRole() != null && reqUser.getRole().getId() != null) {
            Role r = this.roleService.fetchById(reqUser.getRole().getId()).orElse(null);
            currentUser.setRole(r);
        }

        User updated = this.userRepository.save(currentUser);
        return this.userMapper.toUpdateUserDTO(updated);
    }

    @Override
    public ResponseUpdateUserDTO updateMyProfile(ReqUpdateMyProfileDTO reqUser) {
        String currentUserEmail = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userQueryService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.setAddress(reqUser.getAddress());
        currentUser.setGender(reqUser.getGender());
        if (reqUser.getAge() != null) {
            currentUser.setAge(reqUser.getAge());
        }
        currentUser.setName(reqUser.getName());

        if (reqUser.getCompany() != null && reqUser.getCompany().getId() != null) {
            Optional<Company> companyOptional = this.companyService.findById(reqUser.getCompany().getId());
            currentUser.setCompany(companyOptional.orElse(null));
        }

        User updated = this.userRepository.save(currentUser);
        return this.userMapper.toUpdateUserDTO(updated);
    }

    @Override
    @Transactional
    public void deleteUserByIdOrThrow(Long id) {
        User currentUser = this.userQueryService.getUserByIdOrThrow(id);

        PremiumSubscription subscription = this.premiumSubscriptionRepository.findByUser(currentUser).orElse(null);
        boolean isPremium = this.premiumAccessPolicy.isPremium(currentUser, subscription, Instant.now());
        if (isPremium) {
            throw new ConflictException("User đang có quyền prenium, vui lòng thao tác lại sau");
        }

        List<Resume> resumes = this.resumeRepository.findByUser(currentUser);
        if (!resumes.isEmpty()) {
            this.resumeRepository.deleteAll(resumes);
        }
        this.userRepository.deleteById(id);
    }
}