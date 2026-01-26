package com.sprint.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/** 프로젝트 루트의 uploads/thumbnails 폴더를
 * 외부에서 접근할 수 있게 해주는 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로젝트 루트의 uploads/thumbnails 폴더를 /images/** 경로로 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/thumbnails/");
    }

    // 2. 프론트 접속 허용
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173", // ✅ Vite 프론트
                        "http://localhost:3000"  // (CRA 대비용, 있어도 무방)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
