package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.AsignarTurnoRequest;
import com.rojas.fastcash.dto.CrearUsuarioRequest;
import com.rojas.fastcash.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private AdminService adminService;

    @PostMapping("/crear-usuario")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        try {
            Map<String, Object> resultado = adminService.crearUsuario(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            // Devuelve error si el usuario ya existe o si quien pide no es admin
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/asignar-turno")
    public ResponseEntity<?> asignarTurno(@Valid @RequestBody AsignarTurnoRequest request) {
        try {
            Map<String, Object> resultado = adminService.asignarTurno(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}