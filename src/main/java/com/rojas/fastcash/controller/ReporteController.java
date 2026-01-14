package com.rojas.fastcash.controller;

import com.rojas.fastcash.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reportesService;

    // 1. REPORTE GENERAL DE VENTAS (Ruta corregida a "/ventas")
    // Antes se llamaba "/ventas-rango", por eso fallaba
    @GetMapping("/ventas")
    public List<Map<String, Object>> reporteVentas(
            @RequestParam(required = false) String inicio, 
            @RequestParam(required = false) String fin,
            @RequestParam(required = false) Integer usuarioID) {
        return reportesService.obtenerReporteVentas(inicio, fin, usuarioID);
    }

    // 2. REPORTE DE CAJAS (Ruta corregida a "/cajas")
    // Antes se llamaba "/por-caja"
    @GetMapping("/cajas")
    public List<Map<String, Object>> reporteCajas(
            @RequestParam(required = false) String inicio, 
            @RequestParam(required = false) String fin,
            @RequestParam(required = false) Integer usuarioID) {
        return reportesService.obtenerReporteCajas(inicio, fin, usuarioID);
    }

    // 3. GRÁFICOS DASHBOARD
    @GetMapping("/graficos-hoy")
    public Map<String, Object> metricasGraficos(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer usuarioID) {
        return reportesService.obtenerDatosGraficos(fecha, usuarioID);
    }
    
    // 4. CIERRE ACTUAL (Para el ticket)
    @GetMapping("/cierre-actual/{usuarioID}")
    public Map<String, Object> cierreActual(@PathVariable Integer usuarioID) {
        return reportesService.obtenerCierreActual(usuarioID);
    }
}