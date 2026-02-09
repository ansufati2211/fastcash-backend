package com.rojas.fastcash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
// Se eliminaron las importaciones que no se usaban (UrlBasedCorsConfigurationSource y CorsFilter)

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. BEAN DE ENCRIPTACIÓN (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. FILTRO DE SEGURIDAD
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CONFIGURACIÓN CORS (Para que el Frontend se conecte)
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOriginPatterns(List.of("*")); // En producción pon tu dominio https://...
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))
            
            // DESACTIVAR CSRF (Para APIs REST)
            .csrf(csrf -> csrf.disable())
            
            // RUTAS PÚBLICAS
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permitimos todo por ahora
            );

        return http.build();
    }
}