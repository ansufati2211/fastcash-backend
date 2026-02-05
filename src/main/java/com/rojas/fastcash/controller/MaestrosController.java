package com.rojas.fastcash.controller;

import com.rojas.fastcash.entity.*;
import com.rojas.fastcash.service.MaestrosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maestros")
@CrossOrigin(origins = "*") // Importante para permitir acceso desde el Frontend
public class MaestrosController {

    @Autowired
    private MaestrosService maestrosService;

    @GetMapping("/categorias")
    public List<CategoriaVenta> getCategorias() {
        return maestrosService.listarCategoriasActivas();
    }

    @GetMapping("/entidades")
    public List<EntidadFinanciera> getEntidades() {
        return maestrosService.listarEntidadesActivas();
    }

    // CORREGIDO: Cambiado de "/comprobantes" a "/tipos-comprobante"
    // para coincidir con Postman y el Frontend
    @GetMapping("/tipos-comprobante")
    public List<TipoComprobante> getComprobantes() {
        return maestrosService.listarComprobantes();
    }
}