package com.malkollm.jointtasks.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Применить ко всем эндпоинтам
                        .allowedOrigins("http://localhost:5173") // Разрешить доступ с Vue.js
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешить методы
                        .allowedHeaders("*") // Разрешить все заголовки
                        .allowCredentials(true); // Разрешить передачу cookies и авторизационных данных
            }
        };
    }
}
