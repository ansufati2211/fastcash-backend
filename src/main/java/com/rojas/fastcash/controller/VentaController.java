package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.dto.AnulacionRequest; // <--- IMPORTANTE: No olvides importar el nuevo DTO
import com.rojas.fastcash.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            // Intentamos procesar la venta
            Map<String, Object> resultado = ventaService.registrarVenta(request);
            
            // Éxito: Devolvemos 200 OK y los datos del ticket (VentaID, Serie-Numero)
            return ResponseEntity.ok(resultado);
            
        } catch (RuntimeException e) {
            // Error (Caja cerrada, fallo SQL, etc.): Devolvemos 400 Bad Request
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // NUEVO ENDPOINT: MÓDULO 6 (ANULACIONES)
    // ==========================================
    @PostMapping("/anular")
    public ResponseEntity<?> anularVenta(@Valid @RequestBody AnulacionRequest request) {
        try {
            Map<String, Object> resultado = ventaService.anularVenta(request);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}