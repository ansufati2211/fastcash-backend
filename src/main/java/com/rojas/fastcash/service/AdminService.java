package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.AsignarTurnoRequest;
import com.rojas.fastcash.dto.CrearUsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class AdminService {

    @Autowired private JdbcTemplate jdbcTemplate;

    public Map<String, Object> crearUsuario(CrearUsuarioRequest request) {
        // Ejecuta el SP sp_Admin_CrearUsuario
        String sql = "EXEC sp_Admin_CrearUsuario @AdminUsuarioID = ?, @NuevoNombre = ?, @NuevoUsername = ?, @NuevoPasswordHash = ?, @RolID = ?";
        
        return jdbcTemplate.queryForMap(sql, 
            request.getAdminID(),
            request.getNombreCompleto(),
            request.getUsername(),
            request.getPassword(),
            request.getRolID()
        );
    }

    public Map<String, Object> asignarTurno(AsignarTurnoRequest request) {
        // Ejecuta el SP sp_Admin_AsignarTurno
        // Nota: Asigna el turno para la fecha de HOY por defecto
        String sql = "EXEC sp_Admin_AsignarTurno @AdminUsuarioID = ?, @UsuarioID = ?, @TurnoID = ?, @FechaAsignacion = ?";
        
        return jdbcTemplate.queryForMap(sql, 
            request.getAdminID(),
            request.getUsuarioID(),
            request.getTurnoID(),
            LocalDate.now() // Asigna para el día actual
        );
    }
}