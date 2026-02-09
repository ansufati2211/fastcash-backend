package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.*;
import com.rojas.fastcash.entity.Rol;
import com.rojas.fastcash.entity.Usuario;
import com.rojas.fastcash.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects; // ✅ IMPORTANTE: Para eliminar el error de Null Safety

@Service
public class AdminService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ==========================================
    // 1. CREAR USUARIO
    // ==========================================
    public Map<String, Object> crearUsuario(CrearUsuarioRequest req) {
        if (authRepository.findByUsername(req.getUsername()) != null) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }

        Usuario u = new Usuario();
        u.setNombreCompleto(req.getNombreCompleto());
        u.setUsername(req.getUsername());
        
        // Encriptar contraseña
        String hash = passwordEncoder.encode(req.getPassword());
        u.setPassword(hash);

        // Null Safety: Convertir a int primitivo
        int rolId = (req.getRolID() != null) ? req.getRolID().intValue() : 0;
        
        Rol rol = new Rol();
        rol.setRolID(rolId);
        u.setRol(rol);

        u.setActivo(true);
        u.setFechaRegistro(LocalDateTime.now());

        authRepository.save(u);

        return Map.of("mensaje", "Usuario creado correctamente", "status", "OK");
    }

    // ==========================================
    // 2. ACTUALIZAR USUARIO
    // ==========================================
    @Transactional
    public Map<String, Object> actualizarUsuario(ActualizarUsuarioRequest req) {
        // Validación manual primero
        if (req.getUsuarioID() == null) {
            throw new RuntimeException("El ID de usuario es obligatorio.");
        }

        // ✅ SOLUCIÓN DEFINITIVA NULL SAFETY:
        // Objects.requireNonNull satisface al compilador de que el Integer NO es null.
        Usuario u = authRepository.findById(Objects.requireNonNull(req.getUsuarioID()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        u.setNombreCompleto(req.getNombreCompleto());
        u.setUsername(req.getUsername());
        
        if (req.getRolID() != null) {
            Rol rol = new Rol();
            rol.setRolID(req.getRolID().intValue());
            u.setRol(rol);
        }

        if (req.getActivo() != null) {
            u.setActivo(req.getActivo());
        }

        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            if (!req.getPassword().startsWith("$2a$")) {
                String hash = passwordEncoder.encode(req.getPassword());
                u.setPassword(hash);
            }
        }

        authRepository.save(u);

        // Actualizar Turno
        if (req.getTurnoID() != null && req.getTurnoID() > 0) {
            actualizarTurnoUsuario(req.getUsuarioID(), req.getTurnoID());
        }

        return Map.of("mensaje", "Usuario actualizado correctamente", "status", "OK");
    }

    // Método auxiliar privado
    private void actualizarTurnoUsuario(Integer usuarioID, Integer nuevoTurnoID) {
        if (usuarioID == null || nuevoTurnoID == null) return;

        jdbcTemplate.update("UPDATE UsuarioTurnos SET Activo=FALSE WHERE UsuarioID=?", usuarioID);
        
        String sql = "INSERT INTO UsuarioTurnos (UsuarioID, TurnoID, FechaAsignacion, AdminAsignaID, Activo) VALUES (?, ?, NOW(), 1, TRUE)";
        jdbcTemplate.update(sql, usuarioID, nuevoTurnoID);
    }

    // ==========================================
    // 3. ASIGNAR TURNO
    // ==========================================
    public Map<String, Object> asignarTurno(AsignarTurnoRequest req) {
        if (req.getUsuarioID() == null || req.getTurnoID() == null) {
            throw new RuntimeException("UsuarioID y TurnoID son obligatorios");
        }
        actualizarTurnoUsuario(req.getUsuarioID(), req.getTurnoID());
        return Map.of("mensaje", "Turno asignado correctamente", "status", "OK");
    }

    // ==========================================
    // 4. LISTAR USUARIOS
    // ==========================================
    public List<Map<String, Object>> listarTodosLosUsuarios() {
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
            ORDER BY u.UsuarioID ASC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // ==========================================
    // 5. ELIMINAR USUARIO
    // ==========================================
    public void eliminarUsuario(Integer usuarioID) {
        if (usuarioID == null) return;
        
        // ✅ SOLUCIÓN NULL SAFETY AQUÍ TAMBIÉN
        Usuario u = authRepository.findById(Objects.requireNonNull(usuarioID)).orElse(null);
        
        if (u != null) {
            u.setActivo(false);
            authRepository.save(u);
        }
    }
}