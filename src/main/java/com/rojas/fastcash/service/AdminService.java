package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para seguridad
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AdminService {

    @Autowired private JdbcTemplate jdbcTemplate;

    // 1. CREAR USUARIO
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

    // 2. ASIGNAR TURNO (Para asignaciones manuales directas)
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

    // 3. LISTAR USUARIOS (Con Join para ver el Turno actual)
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

    // =========================================================================
    // 4. ACTUALIZAR USUARIO (¡AQUÍ ESTABA EL ERROR Y ESTA ES LA CORRECCIÓN!) 🚨
    // =========================================================================
    @Transactional
    public Map<String, Object> actualizarUsuario(ActualizarUsuarioRequest req) {
        
        // Lógica de seguridad: Si activo es nulo, asumimos true para no bloquear
        boolean estadoFinal = (req.getActivo() != null) ? req.getActivo() : true;

        // Usamos el SP Inteligente que creamos en SQL
        // Este SP actualiza los datos personales Y TAMBIÉN gestiona el cambio de turno si es necesario.
        String sql = "EXEC sp_Admin_ActualizarUsuario " +
                     "@UsuarioID = ?, " +
                     "@Nombre = ?, " +
                     "@Username = ?, " +
                     "@RolID = ?, " +
                     "@TurnoID = ?, " +   // <--- ¡CRUCIAL! Pasamos el turno
                     "@Activo = ?, " +    // <--- ¡CRUCIAL! Pasamos el estado
                     "@Password = ?";
                     
        try {
            jdbcTemplate.update(sql,
                req.getUsuarioID(),
                req.getNombreCompleto(), // Usamos nombreCompleto del DTO
                req.getUsername(),
                req.getRolID(),
                req.getTurnoID(),        // Enviamos el nuevo turno al SQL
                estadoFinal,             // Enviamos el estado (true/false)
                req.getPassword()        // Si es vacío, el SP lo ignora
            );
            
            // Retornamos un mapa de éxito
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario y Turno actualizados correctamente");
            response.put("status", "OK");
            return response;

        } catch (Exception e) {
            // Error amigable
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage());
        }
    }

    // 5. ELIMINAR USUARIO (Desactivación Lógica)
    public void eliminarUsuario(Integer usuarioID) {
        String sql = "UPDATE Usuarios SET Activo = 0 WHERE UsuarioID = ?";
        jdbcTemplate.update(sql, usuarioID);
    }
}