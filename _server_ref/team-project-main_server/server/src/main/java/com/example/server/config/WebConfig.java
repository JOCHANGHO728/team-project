package com.example.server.config;

import com.example.server.security.ManagerAuthenticationInterceptor;
import com.example.server.security.UserAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final ManagerAuthenticationInterceptor managerAuthenticationInterceptor;
    private final UserAuthenticationInterceptor userAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 관리자 권한 인터셉터 등록
        registry.addInterceptor(managerAuthenticationInterceptor)
                .addPathPatterns("/api/v1/managers/**")
                .excludePathPatterns("/api/v1/managers/login");

        // 2. 일반 고객 권한 인터셉터 등록
        registry.addInterceptor(userAuthenticationInterceptor)
                .addPathPatterns("/api/v1/orders/**", "/api/v1/purchase-history/**");
    }
}
