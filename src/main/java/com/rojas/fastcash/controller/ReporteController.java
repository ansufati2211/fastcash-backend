package com.rojas.fastcash.controller;

import com.rojas.fastcash.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reportesService;

    @GetMapping("/ventas")
    public List<Map<String, Object>> reporteVentas(
            @RequestParam(required = false) String inicio, 
            @RequestParam(required = false) String fin,
            @RequestParam(required = false) Integer usuarioID) {
        return reportesService.obtenerReporteVentas(inicio, fin, usuarioID);
    }

    @GetMapping("/cajas")
    public List<Map<String, Object>> reporteCajas(
            @RequestParam(required = false) String inicio, 
            @RequestParam(required = false) String fin,
            @RequestParam(required = false) Integer usuarioID) {
        return reportesService.obtenerReporteCajas(inicio, fin, usuarioID);
    }

    @GetMapping("/graficos-hoy")
    public Map<String, Object> metricasGraficos(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer usuarioID) {
        return reportesService.obtenerDatosGraficos(fecha, usuarioID);
    }
    
    // Endpoint para ver el arqueo (Yape vs Tarjeta vs Efectivo)
    @GetMapping("/cierre-actual/{usuarioID}")
    public Map<String, Object> cierreActual(@PathVariable Integer usuarioID) {
        return reportesService.obtenerCierreActual(usuarioID);
    }

    // Endpoint adicional para compatibilidad con Postman / Query Params
    @GetMapping("/cierre-caja")
    public ResponseEntity<Map<String, Object>> cierreCajaParam(@RequestParam Integer usuarioID) {
        return ResponseEntity.ok(reportesService.obtenerCierreActual(usuarioID));
    }
}