package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.LoginRequest;
import com.rojas.fastcash.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    public Map<String, Object> login(LoginRequest request) {
        // Lógica:
        // 1. Recibimos user y pass en texto plano del Frontend/Postman
        // 2. Los enviamos directo al repositorio (BD)
        
        Map<String, Object> usuario = authRepository.ejecutarSpLogin(
            request.getUsername(), 
            request.getPassword()
        );

        if (usuario == null) {
            throw new RuntimeException("Usuario o contraseña incorrectos.");
        }
        
        return usuario;
    }
}