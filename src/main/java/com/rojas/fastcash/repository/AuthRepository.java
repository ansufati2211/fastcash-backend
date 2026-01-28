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

    // Método para Login
    public Map<String, Object> ejecutarSpLogin(String username, String password) {
        String sql = "EXEC sp_Auth_Login @Username = ?, @PasswordHash = ?";
        List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, username, password);
        if (resultados.isEmpty()) {
            return null;
        }
        return resultados.get(0);
    }

    // =========================================================================
    //  NUEVO MÉTODO: ACTUALIZAR USUARIO (Incluye TurnoID)
    // =========================================================================
    public void actualizarUsuario(Integer id, String nombre, String username, Integer rolId, Integer turnoId, Boolean activo, String password) {
        // Orden exacto de los parámetros en tu SP SQL:
        // @UsuarioID, @Nombre, @Username, @RolID, @TurnoID, @Activo, @Password
        String sql = "EXEC sp_Admin_ActualizarUsuario ?, ?, ?, ?, ?, ?, ?";
        
        // Ejecutamos la actualización
        jdbcTemplate.update(sql, id, nombre, username, rolId, turnoId, activo, password);
    }
}