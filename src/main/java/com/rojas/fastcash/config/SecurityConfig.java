package com.rojas.fastcash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // MODO DESARROLLO: Sin encriptación
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Esto le dice a Spring: "No hagas nada con las contraseñas, úsalas tal cual"
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivar CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permitir todo el tráfico (nuestro AuthController maneja la seguridad)
            );
        return http.build();
    }
}