package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.AperturaCajaRequest;
import com.rojas.fastcash.entity.SesionCaja;
import com.rojas.fastcash.service.CajaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/caja")
@CrossOrigin(origins = "*")
public class CajaController {

    @Autowired
    private CajaService cajaService;

    // GET: Verificar estado de caja del usuario
    @GetMapping("/estado/{usuarioID}")
    public ResponseEntity<?> consultarEstado(@PathVariable Integer usuarioID) {
        SesionCaja sesion = cajaService.obtenerSesionActual(usuarioID);
        if (sesion == null) {
            return ResponseEntity.ok(Map.of("estado", "CERRADO", "mensaje", "Caja cerrada"));
        }
        return ResponseEntity.ok(Map.of("estado", "ABIERTO", "sesion", sesion));
    }

    // POST: Abrir caja
    @PostMapping("/abrir")
    public ResponseEntity<?> abrirCaja(@Valid @RequestBody AperturaCajaRequest request) {
        try {
            cajaService.abrirCaja(request);
            return ResponseEntity.ok(Map.of("mensaje", "Caja abierta exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}