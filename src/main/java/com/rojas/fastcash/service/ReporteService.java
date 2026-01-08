package com.rojas.fastcash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReporteService {

    @Autowired private JdbcTemplate jdbcTemplate;

    public Map<String, Object> obtenerCierreActual(Integer usuarioID) {
        String sql = "EXEC sp_Operacion_ObtenerCierreActual @UsuarioID = ?";
        
        try {
            // queryForMap devuelve un Map con los nombres de columna del SP como claves
            return jdbcTemplate.queryForMap(sql, usuarioID);
        } catch (Exception e) {
            // Si falla (ej: queryForMap no encuentra filas), manejamos el error
            throw new RuntimeException("No se pudo obtener el cierre. Verifique si la caja está abierta.");
        }
    }
}