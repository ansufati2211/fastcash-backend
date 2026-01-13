package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.ActualizarUsuarioRequest;
import com.rojas.fastcash.dto.AsignarTurnoRequest;
import com.rojas.fastcash.dto.CrearUsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired private JdbcTemplate jdbcTemplate;

    public Map<String, Object> crearUsuario(CrearUsuarioRequest request) {
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
        String sql = "EXEC sp_Admin_AsignarTurno @AdminUsuarioID = ?, @UsuarioID = ?, @TurnoID = ?, @FechaAsignacion = ?";
        return jdbcTemplate.queryForMap(sql, 
            request.getAdminID(),
            request.getUsuarioID(),
            request.getTurnoID(),
            LocalDate.now()
        );
    }

    // LISTAR (Muestra todo, incluso si no tienen turno asignado hoy)
    public List<Map<String, Object>> listarTodosLosUsuarios() {
        String sql = """
            SELECT 
                u.UsuarioID, 
                u.NombreCompleto, 
                u.Username, 
                r.Nombre as Rol, 
                u.Activo,
                -- Traemos el Turno y el ID para el Frontend
                ISNULL(t.Nombre, 'Sin Turno') AS TurnoActual,
                ISNULL(t.TurnoID, 1) AS TurnoID
            FROM Usuarios u 
            INNER JOIN Roles r ON u.RolID = r.RolID
            LEFT JOIN UsuarioTurnos ut ON u.UsuarioID = ut.UsuarioID 
                AND ut.Activo = 1
                AND CAST(GETDATE() AS DATE) >= ut.FechaAsignacion
                AND (ut.FechaVigenciaHasta IS NULL OR CAST(GETDATE() AS DATE) <= ut.FechaVigenciaHasta)
            LEFT JOIN Turnos t ON ut.TurnoID = t.TurnoID
            -- Opcional: Si quieres ocultar los eliminados de la lista, descomenta esto:
            -- WHERE u.Activo = 1
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // ACTUALIZAR (Nombre, User, Rol, Password)
    public void actualizarUsuario(ActualizarUsuarioRequest request) {
        String sql = "EXEC sp_Admin_ActualizarUsuario @UsuarioID=?, @Nombre=?, @Username=?, @RolID=?, @Password=?";
        
        // Si el password está vacío o nulo, enviamos NULL para no cambiarlo
        String pass = (request.getPassword() != null && !request.getPassword().trim().isEmpty()) ? request.getPassword() : null;

        jdbcTemplate.update(sql, 
            request.getUsuarioID(),
            request.getNombreCompleto(),
            request.getUsername(),
            request.getRolID(),
            pass
        );
    }

    // ELIMINAR (Soft Delete: Pone Activo = 0)
    public void eliminarUsuario(Integer usuarioID) {
        String sql = "UPDATE Usuarios SET Activo = 0 WHERE UsuarioID = ?";
        jdbcTemplate.update(sql, usuarioID);
    }
}