package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> login(LoginRequest request) {
        // POSTGRES: Usamos SELECT * FROM funcion()
        String sql = "SELECT * FROM sp_auth_login(?, ?)";

        try {
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(
                sql, 
                request.getUsername(), 
                request.getPassword()
            );

            if (resultados.isEmpty()) {
                throw new RuntimeException("Usuario o contraseña incorrectos.");
            }
            
            return resultados.get(0);

        } catch (Exception e) {
            // Limpiamos el mensaje de error de Postgres
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            if (msg.contains("Where:")) msg = msg.split("Where:")[0];
            throw new RuntimeException(msg);
        }
    }
}