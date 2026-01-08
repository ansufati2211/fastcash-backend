package com.rojas.fastcash.controller;

import com.rojas.fastcash.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/cierre-actual/{usuarioID}")
    public ResponseEntity<?> previsualizarCierre(@PathVariable Integer usuarioID) {
        try {
            Map<String, Object> resumen = reporteService.obtenerCierreActual(usuarioID);
            
            if ("CERRADO".equals(resumen.get("Estado"))) {
                return ResponseEntity.ok(Map.of("mensaje", "El usuario no tiene caja abierta actualmente."));
            }
            
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}