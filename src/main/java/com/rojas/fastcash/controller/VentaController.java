package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor 
public class VentaController {

    private final VentaService ventaService; 

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVenta(@RequestBody RegistroVentaRequest request) {
        try {
            Map<String, Object> resultado = ventaService.registrarVenta(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "Status", "ERROR", 
                "Mensaje", e.getMessage()
            ));
        }
    }

    @GetMapping("/historial/{usuarioID}")
    public ResponseEntity<List<Map<String, Object>>> listarHistorial(
            @PathVariable Integer usuarioID,
            @RequestParam(name = "filtro", required = false) Integer filtro,
            @RequestParam(name = "medioPago", required = false) String medioPago
    ) {
        List<Map<String, Object>> historial = ventaService.listarHistorialDia(usuarioID, filtro, medioPago);
        return ResponseEntity.ok(historial);
    }

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