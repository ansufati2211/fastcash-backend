package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired private JdbcTemplate jdbcTemplate;

    // 1. CREAR USUARIO (Ahora devuelve Map para que el Controller no falle)
    public Map<String, Object> crearUsuario(CrearUsuarioRequest req) {
        String sql = "INSERT INTO Usuarios (NombreCompleto, Username, Pswd, RolID, Activo, FechaRegistro) VALUES (?, ?, ?, ?, 1, GETDATE())";
        
        jdbcTemplate.update(sql, 
            req.getNombreCompleto(), 
            req.getUsername(), 
            req.getPassword(), 
            req.getRolID()
        );
        
        return Map.of("mensaje", "Usuario creado correctamente", "status", "OK");
    }

    // 2. ASIGNAR TURNO (Ahora devuelve Map)
    public Map<String, Object> asignarTurno(AsignarTurnoRequest req) {
        // A. Desactivar turnos anteriores
        jdbcTemplate.update("UPDATE UsuarioTurnos SET Activo=0 WHERE UsuarioID=?", req.getUsuarioID());
        
        // B. Insertar nuevo
        String sql = "INSERT INTO UsuarioTurnos (UsuarioID, TurnoID, FechaAsignacion, AdminAsignaID, Activo) VALUES (?, ?, GETDATE(), ?, 1)";
        
        jdbcTemplate.update(sql,
                req.getUsuarioID(), 
                req.getTurnoID(), 
                req.getAdminID()
        );

        return Map.of("mensaje", "Turno asignado correctamente", "status", "OK");
    }

    // 3. LISTAR USUARIOS
    public List<Map<String, Object>> listarTodosLosUsuarios() {
        String sql = """
            SELECT u.UsuarioID, u.NombreCompleto, u.Username, r.Nombre as Rol, u.Activo,
                   ISNULL(t.Nombre, 'Sin Turno') AS TurnoActual,
                   ISNULL(t.TurnoID, 0) AS TurnoID
            FROM Usuarios u 
            JOIN Roles r ON u.RolID = r.RolID
            LEFT JOIN (
                SELECT UsuarioID, TurnoID, ROW_NUMBER() OVER(PARTITION BY UsuarioID ORDER BY FechaAsignacion DESC) as rn
                FROM UsuarioTurnos WHERE Activo = 1
            ) ult ON u.UsuarioID = ult.UsuarioID AND ult.rn = 1
            LEFT JOIN Turnos t ON ult.TurnoID = t.TurnoID
            ORDER BY u.UsuarioID DESC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // 4. ACTUALIZAR USUARIO
    public void actualizarUsuario(ActualizarUsuarioRequest req) {
        String pass = req.getPassword();
        Boolean activo = req.getActivo() != null ? req.getActivo() : true; // Default true si es nulo

        if (pass != null && !pass.trim().isEmpty()) {
            // Con contraseña
            String sql = "UPDATE Usuarios SET NombreCompleto=?, Username=?, RolID=?, Activo=?, Pswd=? WHERE UsuarioID=?";
            jdbcTemplate.update(sql, 
                req.getNombreCompleto(), 
                req.getUsername(), 
                req.getRolID(), 
                activo, 
                pass, 
                req.getUsuarioID()
            );
        } else {
            // Sin contraseña
            String sql = "UPDATE Usuarios SET NombreCompleto=?, Username=?, RolID=?, Activo=? WHERE UsuarioID=?";
            jdbcTemplate.update(sql, 
                req.getNombreCompleto(), 
                req.getUsername(), 
                req.getRolID(), 
                activo, 
                req.getUsuarioID()
            );
        }
    }

    // 5. ELIMINAR USUARIO (Este era el método "undefined")
    public void eliminarUsuario(Integer usuarioID) {
        // Desactivación lógica (Soft Delete)
        String sql = "UPDATE Usuarios SET Activo = 0 WHERE UsuarioID = ?";
        jdbcTemplate.update(sql, usuarioID);
    }
}