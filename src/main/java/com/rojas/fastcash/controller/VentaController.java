package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*") // Permite conexiones desde cualquier origen (Frontend)
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // =========================================================================
    // 1. REGISTRAR VENTA
    // =========================================================================
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVenta(@RequestBody RegistroVentaRequest request) {
        try {
            Map<String, Object> resultado = ventaService.registrarVenta(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            // Devolvemos un JSON de error estructurado
            return ResponseEntity.badRequest().body(Map.of(
                "Status", "ERROR", 
                "Mensaje", e.getMessage()
            ));
        }
    }

    // =========================================================================
    // 2. LISTAR HISTORIAL (CORREGIDO EL FILTRO) 🔍
    // =========================================================================
    @GetMapping("/historial/{usuarioID}")
    public ResponseEntity<List<Map<String, Object>>> listarHistorial(
            @PathVariable Integer usuarioID,
            // 👇 AQUÍ ESTABA EL DETALLE: Debemos capturar el parámetro "?filtro=X"
            @RequestParam(name = "filtro", required = false) Integer filtro
    ) {
        // Ahora sí le pasamos el filtro (o null) al servicio
        List<Map<String, Object>> historial = ventaService.listarHistorialDia(usuarioID, filtro);
        return ResponseEntity.ok(historial);
    }

    // =========================================================================
    // 3. ANULAR VENTA
    // =========================================================================
    @PostMapping("/anular")
    public ResponseEntity<?> anularVenta(@RequestBody AnulacionRequest request) {
        try {
            Map<String, Object> resultado = ventaService.anularVenta(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}