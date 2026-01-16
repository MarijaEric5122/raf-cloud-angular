package com.example.backend_MarijaNatasa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class UserSecurityConfig implements WebMvcConfigurer {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // DODAJEM OVO ZA CORS (da bi Angular mogao da komunicira sa Backendom)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Dozvoli sve rute
                .allowedOrigins("http://localhost:4200") // Port mog Angulara
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
