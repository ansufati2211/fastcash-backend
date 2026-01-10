package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier Frontend (Localhost)
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // ==========================================
    // ENDPOINT 1: REGISTRAR VENTA
    // Soporta: Pagos Yape/Tarjeta y Fecha Manual (si viene en el JSON)
    // ==========================================
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVenta(@Valid @RequestBody RegistroVentaRequest request) {
        try {
            // Intentamos procesar la venta llamando al Service (que ahora maneja fecha y validaciones)
            Map<String, Object> resultado = ventaService.registrarVenta(request);
            
            // Éxito: Devolvemos 200 OK y los datos del ticket (VentaID, Serie-Numero)
            return ResponseEntity.ok(resultado);
            
        } catch (RuntimeException e) {
            // Error de negocio (Caja cerrada, falta código Yape, etc.)
            // Devolvemos 400 Bad Request para que el Frontend muestre la alerta roja
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Error inesperado
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    // ==========================================
    // ENDPOINT 2: ANULAR VENTA
    // Permite cancelar una venta si hubo error (Solo si no está anulada ya)
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