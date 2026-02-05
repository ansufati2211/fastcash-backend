package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AdminService {

    @Autowired private JdbcTemplate jdbcTemplate;

    // 1. CREAR USUARIO
    public Map<String, Object> crearUsuario(CrearUsuarioRequest req) {
        // POSTGRES: Usamos NOW() en lugar de GETDATE()
        String sql = "INSERT INTO Usuarios (NombreCompleto, Username, Pswd, RolID, Activo, FechaRegistro) VALUES (?, ?, ?, ?, TRUE, NOW())";
        
        jdbcTemplate.update(sql, 
            req.getNombreCompleto(), 
            req.getUsername(), 
            req.getPassword(), 
            req.getRolID()
        );
        
        return Map.of("mensaje", "Usuario creado correctamente", "status", "OK");
    }

    // 2. ASIGNAR TURNO
    public Map<String, Object> asignarTurno(AsignarTurnoRequest req) {
        jdbcTemplate.update("UPDATE UsuarioTurnos SET Activo=FALSE WHERE UsuarioID=?", req.getUsuarioID());
        
        // POSTGRES: Usamos NOW()
        String sql = "INSERT INTO UsuarioTurnos (UsuarioID, TurnoID, FechaAsignacion, AdminAsignaID, Activo) VALUES (?, ?, NOW(), ?, TRUE)";
        
        jdbcTemplate.update(sql,
                req.getUsuarioID(), 
                req.getTurnoID(), 
                req.getAdminID()
        );

        return Map.of("mensaje", "Turno asignado correctamente", "status", "OK");
    }

    // 3. LISTAR USUARIOS
    public List<Map<String, Object>> listarTodosLosUsuarios() {
        // POSTGRES: Usamos COALESCE en lugar de ISNULL
        String sql = """
            SELECT u.UsuarioID, u.NombreCompleto, u.Username, r.Nombre as Rol, u.Activo,
                   COALESCE(t.Nombre, 'Sin Turno') AS TurnoActual,
                   COALESCE(t.TurnoID, 0) AS TurnoID
            FROM Usuarios u 
            JOIN Roles r ON u.RolID = r.RolID
            LEFT JOIN (
                SELECT UsuarioID, TurnoID, ROW_NUMBER() OVER(PARTITION BY UsuarioID ORDER BY FechaAsignacion DESC) as rn
                FROM UsuarioTurnos WHERE Activo = TRUE
            ) ult ON u.UsuarioID = ult.UsuarioID AND ult.rn = 1
            LEFT JOIN Turnos t ON ult.TurnoID = t.TurnoID
            ORDER BY u.UsuarioID DESC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // 4. ACTUALIZAR USUARIO
    @Transactional
    public Map<String, Object> actualizarUsuario(ActualizarUsuarioRequest req) {
        boolean estadoFinal = (req.getActivo() != null) ? req.getActivo() : true;

        // POSTGRES: Llamada a función
        String sql = "SELECT sp_admin_actualizarusuario(?, ?, ?, ?, ?, ?, ?)";
                     
        try {
            jdbcTemplate.execute(sql, (java.sql.PreparedStatement ps) -> {
                ps.setInt(1, req.getUsuarioID());
                ps.setString(2, req.getNombreCompleto());
                ps.setString(3, req.getUsername());
                ps.setInt(4, req.getRolID());
                ps.setInt(5, req.getTurnoID());
                ps.setBoolean(6, estadoFinal);
                ps.setString(7, req.getPassword());
                return ps.execute();
            });
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario y Turno actualizados correctamente");
            response.put("status", "OK");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage());
        }
    }

    // 5. ELIMINAR USUARIO
    public void eliminarUsuario(Integer usuarioID) {
        String sql = "UPDATE Usuarios SET Activo = FALSE WHERE UsuarioID = ?";
        jdbcTemplate.update(sql, usuarioID);
    }
}