package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.LoginRequest;
import com.rojas.fastcash.entity.Usuario;
import com.rojas.fastcash.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> login(LoginRequest request) {
        
        // 1. BUSCAR USUARIO
        Usuario usuario = authRepository.findByUsername(request.getUsername());

        // 2. VALIDACIONES
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RuntimeException("El usuario se encuentra inactivo.");
        }

        // 3. COMPARAR CONTRASEÑA
        // CORRECCIÓN: Usamos .getPassword() que es el getter generado por Lombok para el campo 'password'
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        // 4. RESPUESTA EXITOSA
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("UsuarioID", usuario.getUsuarioID());
        respuesta.put("NombreCompleto", usuario.getNombreCompleto());
        respuesta.put("Username", usuario.getUsername());
        
        String rolNombre = (usuario.getRol() != null) ? usuario.getRol().getNombre() : "SIN_ROL";
        respuesta.put("Rol", rolNombre);
        
        respuesta.put("Mensaje", "Login Exitoso");

        return respuesta;
    }
}