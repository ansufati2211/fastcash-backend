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
import java.util.Objects; // ✅ IMPORTANTE

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
    @Transactional
    public Map<String, Object> crearUsuario(CrearUsuarioRequest req) {
        if (authRepository.findByUsername(req.getUsername()) != null) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }

        Usuario u = new Usuario();
        u.setNombreCompleto(req.getNombreCompleto());
        u.setUsername(req.getUsername());
        
        // Encriptar contraseña
        u.setPassword(passwordEncoder.encode(req.getPassword()));

        // ✅ Null Safety para Rol (Default: Cajero=2)
        int rolIdSafe = (req.getRolId() != null) ? req.getRolId().intValue() : 2;
        
        Rol rol = new Rol();
        rol.setRolID(rolIdSafe);
        u.setRol(rol);

        u.setActivo(true);
        u.setFechaRegistro(LocalDateTime.now());

        Usuario usuarioGuardado = authRepository.save(u);

        // ✅ Null Safety para Turno
        if (req.getTurnoId() != null && req.getTurnoId().intValue() > 0) {
            actualizarTurnoUsuario(usuarioGuardado.getUsuarioID(), req.getTurnoId());
        }

        return Map.of("mensaje", "Usuario creado correctamente", "status", "OK");
    }

    // ==========================================
    // 2. ACTUALIZAR USUARIO
    // ==========================================
    @Transactional
    public Map<String, Object> actualizarUsuario(ActualizarUsuarioRequest req) {
        // ✅ Validación estricta ID
        Integer idRecibido = req.getUsuarioId();
        if (idRecibido == null) {
            throw new RuntimeException("El ID de usuario es obligatorio.");
        }

        Usuario u = authRepository.findById(Objects.requireNonNull(idRecibido))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        u.setNombreCompleto(req.getNombreCompleto());
        u.setUsername(req.getUsername());
        
        // ✅ Null Safety Rol
        if (req.getRolId() != null) {
            Rol rol = new Rol();
            rol.setRolID(req.getRolId().intValue());
            u.setRol(rol);
        }

        if (req.getActivo() != null) {
            u.setActivo(req.getActivo());
        }

        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            if (!req.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode(req.getPassword()));
            }
        }

        authRepository.save(u);

        // ✅ Null Safety Turno
        if (req.getTurnoId() != null && req.getTurnoId().intValue() > 0) {
            actualizarTurnoUsuario(req.getUsuarioId(), req.getTurnoId());
        }

        return Map.of("mensaje", "Usuario actualizado correctamente", "status", "OK");
    }

    // ==========================================
    // 3. ACTUALIZAR TURNO (CORREGIDO PARA EVITAR DUPLICADOS)
    // ==========================================
    private void actualizarTurnoUsuario(Integer usuarioID, Integer nuevoTurnoID) {
        if (usuarioID == null || nuevoTurnoID == null) return;

        // A. Obtener fecha de hoy
        java.sql.Date fechaHoy = java.sql.Date.valueOf(java.time.LocalDate.now());

        // B. Verificar si YA existe un turno hoy
        String checkSql = "SELECT COUNT(*) FROM UsuarioTurnos WHERE UsuarioID = ? AND FechaAsignacion = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, usuarioID, fechaHoy);

        if (count != null && count > 0) {
            // C. Si existe: UPDATE
            String updateSql = "UPDATE UsuarioTurnos SET TurnoID = ?, Activo = TRUE WHERE UsuarioID = ? AND FechaAsignacion = ?";
            jdbcTemplate.update(updateSql, nuevoTurnoID, usuarioID, fechaHoy);
        } else {
            // D. Si no existe: DESACTIVAR ANTERIORES e INSERTAR NUEVO
            jdbcTemplate.update("UPDATE UsuarioTurnos SET Activo=FALSE WHERE UsuarioID=?", usuarioID);

            String insertSql = """
                INSERT INTO UsuarioTurnos (UsuarioID, TurnoID, FechaAsignacion, AdminAsignaID, Activo) 
                VALUES (?, ?, ?, (SELECT UsuarioID FROM Usuarios WHERE RolID = 1 AND Activo = TRUE ORDER BY UsuarioID ASC LIMIT 1), TRUE)
            """;
            // Pasamos fechaHoy explícitamente
            jdbcTemplate.update(insertSql, usuarioID, nuevoTurnoID, fechaHoy);
        }
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
    // 5. ELIMINAR Y ASIGNAR
    // ==========================================
    public void eliminarUsuario(Integer usuarioID) {
        if (usuarioID == null) return;
        Usuario u = authRepository.findById(Objects.requireNonNull(usuarioID)).orElse(null);
        if (u != null) {
            u.setActivo(false);
            authRepository.save(u);
        }
    }
    
    public Map<String, Object> asignarTurno(AsignarTurnoRequest req) {
        if (req.getUsuarioID() == null || req.getTurnoID() == null) {
            throw new RuntimeException("Datos incompletos");
        }
        actualizarTurnoUsuario(req.getUsuarioID(), req.getTurnoID());
        return Map.of("mensaje", "Turno asignado", "status", "OK");
    }
}