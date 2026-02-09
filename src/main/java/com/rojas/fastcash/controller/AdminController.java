package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.*;
import com.rojas.fastcash.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ==========================================
    // 1. CREAR USUARIO
    // URL: POST http://localhost:8080/api/admin/usuario
    // ==========================================
    @PostMapping("/usuario") 
    public ResponseEntity<Map<String, Object>> crearUsuario(@RequestBody CrearUsuarioRequest req) {
        return ResponseEntity.ok(adminService.crearUsuario(req));
    }

    // ==========================================
    // 2. LISTAR USUARIOS
    // URL: GET http://localhost:8080/api/admin/usuarios
    // ==========================================
    @GetMapping("/usuarios")
    public ResponseEntity<List<Map<String, Object>>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarTodosLosUsuarios());
    }

    // ==========================================
    // 3. ACTUALIZAR USUARIO
    // URL: PUT http://localhost:8080/api/admin/usuario
    // ==========================================
    @PutMapping("/usuario")
    public ResponseEntity<Map<String, Object>> actualizarUsuario(@RequestBody ActualizarUsuarioRequest req) {
        return ResponseEntity.ok(adminService.actualizarUsuario(req));
    }

    // ==========================================
    // 4. ASIGNAR TURNO
    // URL: POST http://localhost:8080/api/admin/turno
    // ==========================================
    @PostMapping("/turno")
    public ResponseEntity<Map<String, Object>> asignarTurno(@RequestBody AsignarTurnoRequest req) {
        return ResponseEntity.ok(adminService.asignarTurno(req));
    }

    // ==========================================
    // 5. ELIMINAR USUARIO
    // URL: DELETE http://localhost:8080/api/admin/usuario/{id}
    // ==========================================
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        adminService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }
}