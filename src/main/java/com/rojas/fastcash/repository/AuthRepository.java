package com.rojas.fastcash.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AuthRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> ejecutarSpLogin(String username, String password) {
        // Llamada al SP definido en SQL Server
        // Se asume que en BD la columna Pswd tiene el texto plano (ej: "123456")
        String sql = "EXEC sp_Auth_Login @Username = ?, @PasswordHash = ?";
        
        // Ejecutamos la consulta
        List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, username, password);

        // Si no hay resultados, las credenciales no coincidieron
        if (resultados.isEmpty()) {
            return null;
        }
        
        // Retornamos el primer registro encontrado
        return resultados.get(0);
    }
}