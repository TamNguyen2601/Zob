package com.github.TamNguyen.Zob.config;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.github.TamNguyen.Zob.domain.Permission;
import com.github.TamNguyen.Zob.domain.Role;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.service.user.UserQueryService;
import com.github.TamNguyen.Zob.util.SecurityUtil;
import com.github.TamNguyen.Zob.util.error.PermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {

    private static final String COMPANY_PATH = "/api/v1/companies";
    private static final String COMPANY_DETAIL_PATH = "/api/v1/companies/{id}";
    private static final String COMPANY_JOBS_PATH = "/api/v1/companies/{companyId}/jobs";
    private static final String JOB_PATH = "/api/v1/jobs";
    private static final String JOB_DETAIL_PATH = "/api/v1/jobs/{id}";
    private static final String SKILL_PATH = "/api/v1/skills";
    private static final String SKILL_DETAIL_PATH = "/api/v1/skills/{id}";
    private static final String FILE_PATH = "/api/v1/files";
    private static final String RESUME_PATH = "/api/v1/resumes";
    private static final String RESUME_DETAIL_PATH = "/api/v1/resumes/{id}";
    private static final String RESUME_BY_USER_PATH = "/api/v1/resumes/by-user";
    private static final String USER_SELF_PROFILE_PATH = "/api/v1/users/me";
    private static final String PREMIUM_PURCHASE_PATH = "/api/v1/premium/purchase";
    private static final String PREMIUM_ME_PATH = "/api/v1/premium/me";
    private static final String VNPAY_IPN_PATH = "/api/v1/payments/vnpay/ipn";
    private static final String VNPAY_RETURN_PATH = "/api/v1/payments/vnpay/return";
    private static final String CHAT_PATH = "/api/v1/chat";

    private final UserQueryService userQueryService;

    public PermissionInterceptor(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();

        if (isPublicReadRequest(path, httpMethod)) {
            return true;
        }

        if (isPublicPaymentCallback(path, httpMethod)) {
            return true;
        }

        if (isFileRequest(path)) {
            return true;
        }

        // check permission
        Optional<String> currentUserLogin = SecurityUtil.getCurrentUserLogin();
        if (currentUserLogin.isEmpty() || !StringUtils.hasText(currentUserLogin.get())) {
            throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
        }

        Optional<User> userOptional = this.userQueryService.findByEmail(currentUserLogin.get());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (isResumeRequestAllowedForAuthenticatedUser(path, httpMethod)) {
                return true;
            }
            if (isSelfProfileUpdateAllowedForAuthenticatedUser(path, httpMethod)) {
                return true;
            }
            if (isPremiumRequestAllowedForAuthenticatedUser(path, httpMethod)) {
                return true;
            }
            if (isChatRequestAllowedForAuthenticatedUser(path, httpMethod)) {
                return true;
            }

            Role role = user.getRole();
            if (role != null) {
                if (!role.isActive()) {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                }

                List<Permission> permissions = role.getPermissions();
                boolean isAllow = permissions != null
                        && permissions.stream().anyMatch(item -> item.getApiPath().equals(path)
                                && item.getMethod().equals(httpMethod));

                if (isAllow == false) {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                }
            } else {
                throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
            }
        }

        return true;
    }

    private boolean isPublicReadRequest(String path, String httpMethod) {
        if (!"GET".equalsIgnoreCase(httpMethod) || path == null) {
            return false;
        }

        return COMPANY_PATH.equals(path)
                || COMPANY_DETAIL_PATH.equals(path)
                || COMPANY_JOBS_PATH.equals(path)
                || JOB_PATH.equals(path)
                || JOB_DETAIL_PATH.equals(path)
                || SKILL_PATH.equals(path)
                || SKILL_DETAIL_PATH.equals(path);
    }

    private boolean isFileRequest(String path) {
        return FILE_PATH.equals(path);
    }

    private boolean isResumeRequestAllowedForAuthenticatedUser(String path, String httpMethod) {
        return (RESUME_PATH.equals(path) && "POST".equalsIgnoreCase(httpMethod))
                || (RESUME_BY_USER_PATH.equals(path) && "POST".equalsIgnoreCase(httpMethod))
                || (RESUME_DETAIL_PATH.equals(path) && "DELETE".equalsIgnoreCase(httpMethod));
    }

    private boolean isSelfProfileUpdateAllowedForAuthenticatedUser(String path, String httpMethod) {
        return USER_SELF_PROFILE_PATH.equals(path) && "PUT".equalsIgnoreCase(httpMethod);
    }

    private boolean isPremiumRequestAllowedForAuthenticatedUser(String path, String httpMethod) {
        return (PREMIUM_PURCHASE_PATH.equals(path) && "POST".equalsIgnoreCase(httpMethod))
                || (PREMIUM_ME_PATH.equals(path) && "GET".equalsIgnoreCase(httpMethod))
                || ("/api/v1/jobs/{id}/resumes/stats".equals(path) && "GET".equalsIgnoreCase(httpMethod));
    }

    /**
     * Chatbox AI: cho phép tất cả user đã đăng nhập sử dụng,
     * không phụ thuộc vào permission role trong database.
     */
    private boolean isChatRequestAllowedForAuthenticatedUser(String path, String httpMethod) {
        return CHAT_PATH.equals(path) && "POST".equalsIgnoreCase(httpMethod);
    }

    private boolean isPublicPaymentCallback(String path, String httpMethod) {
        if (!"GET".equalsIgnoreCase(httpMethod)) {
            return false;
        }

        return VNPAY_IPN_PATH.equals(path) || VNPAY_RETURN_PATH.equals(path);
    }
}
