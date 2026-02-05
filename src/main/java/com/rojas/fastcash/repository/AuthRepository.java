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
        // POSTGRES: SELECT * FROM function
        String sql = "SELECT * FROM sp_auth_login(?, ?)";
        List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, username, password);
        if (resultados.isEmpty()) {
            return null;
        }
        return resultados.get(0);
    }

    public void actualizarUsuario(Integer id, String nombre, String username, Integer rolId, Integer turnoId, Boolean activo, String password) {
        // POSTGRES: Llamada a función
        String sql = "SELECT sp_admin_actualizarusuario(?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, id, nombre, username, rolId, turnoId, activo, password);
    }
}