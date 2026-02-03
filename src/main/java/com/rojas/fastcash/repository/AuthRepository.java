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

    // LOGIN
    public Map<String, Object> ejecutarSpLogin(String username, String password) {
        // CAMBIO POSTGRES:
        // 1. Usamos SELECT * FROM funcion()
        // 2. Quitamos los nombres de parámetros (@Username=) porque JDBC usa posición (?)
        String sql = "SELECT * FROM sp_auth_login(?, ?)";
        
        try {
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, username, password);
            
            if (resultados.isEmpty()) {
                return null;
            }
            return resultados.get(0);
        } catch (Exception e) {
            // Postgres lanza error si las credenciales fallan (por el RAISE EXCEPTION del script)
            return null; 
        }
    }

    // ACTUALIZAR USUARIO
    public void actualizarUsuario(Integer id, String nombre, String username, Integer rolId, Integer turnoId, Boolean activo, String password) {
        // CAMBIO POSTGRES:
        // Llamada a función VOID con SELECT
        String sql = "SELECT sp_admin_actualizarusuario(?, ?, ?, ?, ?, ?, ?)";
        
        // Ejecutamos la actualización (en Postgres usamos execute o query, update funciona pero es semántica)
        jdbcTemplate.update(sql, id, nombre, username, rolId, turnoId, activo, password);
    }
}