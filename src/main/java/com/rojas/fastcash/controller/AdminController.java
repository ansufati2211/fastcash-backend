package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.ActualizarUsuarioRequest;
import com.rojas.fastcash.dto.AsignarTurnoRequest;
import com.rojas.fastcash.dto.CrearUsuarioRequest;
import com.rojas.fastcash.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private AdminService adminService;

    // 1. CREAR USUARIO
    @PostMapping("/crear-usuario")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        try {
            Map<String, Object> resultado = adminService.crearUsuario(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. ASIGNAR TURNO
    @PostMapping("/asignar-turno")
    public ResponseEntity<?> asignarTurno(@Valid @RequestBody AsignarTurnoRequest request) {
        try {
            Map<String, Object> resultado = adminService.asignarTurno(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. LISTAR USUARIOS
    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        try {
            List<Map<String, Object>> usuarios = adminService.listarTodosLosUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 4. ACTUALIZAR USUARIO
    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarUsuario(@RequestBody ActualizarUsuarioRequest request) {
        try {
            adminService.actualizarUsuario(request);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario actualizado correctamente"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. ELIMINAR (DESACTIVAR) USUARIO
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            adminService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario desactivado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}