package com.rojas.fastcash.controller;

import com.rojas.fastcash.dto.CategoriaRequest;
import com.rojas.fastcash.dto.EntidadFinancieraRequest;
import com.rojas.fastcash.entity.CategoriaVenta;
import com.rojas.fastcash.entity.EntidadFinanciera;
import com.rojas.fastcash.entity.Rol;
import com.rojas.fastcash.entity.Turno;
import com.rojas.fastcash.service.MaestrosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maestros")
@CrossOrigin(origins = "*") // Importante para que el frontend conecte sin líos
public class MaestrosController {

    @Autowired
    private MaestrosService maestrosService;

    // --- GETs EXISTENTES ---
    @GetMapping("/roles")
    public List<Rol> listarRoles() { return maestrosService.listarRoles(); }

    @GetMapping("/turnos")
    public List<Turno> listarTurnos() { return maestrosService.listarTurnos(); }

    @GetMapping("/categorias")
    public List<CategoriaVenta> listarCategorias() { return maestrosService.listarCategorias(); }

    @GetMapping("/entidades")
    public List<EntidadFinanciera> listarEntidades() { return maestrosService.listarEntidades(); }

    // --- NUEVOS ENDPOINTS: GESTIÓN DE CATEGORÍAS 🛠️ ---

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaVenta> crearCategoria(@RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(maestrosService.crearCategoria(request));
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaVenta> actualizarCategoria(@PathVariable Integer id, @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(maestrosService.actualizarCategoria(id, request));
    }

    // --- NUEVOS ENDPOINTS: GESTIÓN DE ENTIDADES 🏦 ---

    @PostMapping("/entidades")
    public ResponseEntity<EntidadFinanciera> crearEntidad(@RequestBody EntidadFinancieraRequest request) {
        return ResponseEntity.ok(maestrosService.crearEntidad(request));
    }

    @PutMapping("/entidades/{id}")
    public ResponseEntity<EntidadFinanciera> actualizarEntidad(@PathVariable Integer id, @RequestBody EntidadFinancieraRequest request) {
        return ResponseEntity.ok(maestrosService.actualizarEntidad(id, request));
    }
}