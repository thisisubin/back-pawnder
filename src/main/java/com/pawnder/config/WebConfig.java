package com.pawnder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//MVC
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:/Users/thisisubin/Desktop/pawnder/uploads/}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://98.83.89.13")
                .allowedMethods("*")
                .allowCredentials(true);
    }

    // ⭐ 정적 리소스 핸들링 설정 추가
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** → 실제 파일 시스템 경로로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
