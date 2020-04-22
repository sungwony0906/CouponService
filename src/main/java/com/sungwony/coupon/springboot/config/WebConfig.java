package com.sungwony.coupon.springboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;
    private String[] INTERCEPTOR_WHITE_LIST = {
            "/api/signUp/**",
            "/api/signIn/**",
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/*")
                .excludePathPatterns("/api/signUp", "/api/signIn");
    }
}
