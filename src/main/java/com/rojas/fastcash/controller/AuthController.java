package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.LoginRequest;
import com.rojas.fastcash.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Importante para cuando conectes el Frontend (React/Angular)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Intentamos loguear
            Map<String, Object> respuesta = authService.login(request);
            
            // Éxito: 200 OK + JSON con datos del usuario
            return ResponseEntity.ok(respuesta);
            
        } catch (RuntimeException e) {
            // Error: 401 Unauthorized + Mensaje
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}