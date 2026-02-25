package com.rojas.fastcash.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull; // 🚀 IMPORTANTE: Importamos la anotación
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class ApiTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,   // 🚀 Añadimos @NonNull
            @NonNull HttpServletResponse response, // 🚀 Añadimos @NonNull
            @NonNull FilterChain filterChain)      // 🚀 Añadimos @NonNull
            throws ServletException, IOException {

        // 1. Extraemos la cabecera Authorization enviada por el Frontend
        String authHeader = request.getHeader("Authorization");

        // 2. Verificamos si tiene nuestro token temporal
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Quitamos la palabra "Bearer "

            // 3. Si el token es correcto, le decimos a Spring Security "Déjalo pasar"
            if ("token-temporal-hasta-implementar-jwt".equals(token)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken("usuarioValido", null, new ArrayList<>());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Guardamos el pase válido en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 4. Continuar con la petición
        filterChain.doFilter(request, response);
    }
}
