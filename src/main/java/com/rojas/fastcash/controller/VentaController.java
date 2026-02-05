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
@CrossOrigin(origins = "*") // Permite peticiones desde el Frontend
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // 1. REGISTRAR VENTA
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

    // 2. ANULAR VENTA
    @PostMapping("/anular")
    public ResponseEntity<?> anularVenta(@Valid @RequestBody AnulacionRequest request) {
        try {
            Map<String, Object> resultado = ventaService.anularVenta(request);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. HISTORIAL DEL DÍA (CORREGIDO PARA TU PRUEBA)
    // Este es el que faltaba para que funcione /api/ventas/historial-dia?usuarioID=1
    @GetMapping("/historial-dia")
    public ResponseEntity<?> obtenerHistorialDia(
            @RequestParam Integer usuarioID,
            @RequestParam(value = "filtro", required = false) Integer filtroUsuarioID
    ) {
        try {
            List<Map<String, Object>> historial = ventaService.listarHistorialDia(usuarioID, filtroUsuarioID);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 4. HISTORIAL (Ruta alternativa por ID en URL, por si acaso)
    @GetMapping("/historial/{usuarioID}")
    public ResponseEntity<?> obtenerHistorial(@PathVariable Integer usuarioID) {
        try {
            List<Map<String, Object>> historial = ventaService.listarHistorialDia(usuarioID, null);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}