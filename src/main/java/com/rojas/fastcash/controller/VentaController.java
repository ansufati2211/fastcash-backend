package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVenta(@Valid @RequestBody RegistroVentaRequest request) {
        try {
            Map<String, Object> resultado = ventaService.registrarVenta(request);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/anular")
    public ResponseEntity<?> anularVenta(@Valid @RequestBody AnulacionRequest request) {
        try {
            Map<String, Object> resultado = ventaService.anularVenta(request);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // NUEVO ENDPOINT: HISTORIAL
    // ==========================================
    @GetMapping("/historial/{usuarioID}")
    public ResponseEntity<?> obtenerHistorial(@PathVariable Integer usuarioID) {
        try {
            List<Map<String, Object>> historial = ventaService.listarHistorialDia(usuarioID);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
}