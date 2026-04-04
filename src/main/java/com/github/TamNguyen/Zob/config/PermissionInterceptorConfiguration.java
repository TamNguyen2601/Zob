package com.github.TamNguyen.Zob.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.TamNguyen.Zob.service.user.UserQueryService;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    private final UserQueryService userQueryService;

    public PermissionInterceptorConfiguration(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor(this.userQueryService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/",
                "/api/v1/auth/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api/v1/files/**",
        };

        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}
